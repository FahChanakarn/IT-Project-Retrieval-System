package com.springmvc.manager;

import com.springmvc.model.DownloadToken;
import com.springmvc.model.HibernateConnection;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

public class DownloadTokenManager {

	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	private static final int TOKEN_LENGTH = 32;

	// สร้าง download token
	public String createDownloadToken(int fileId, int daysValid) {
		SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
		Session session = sessionFactory.openSession();
		session.beginTransaction();

		try {
			// สร้าง token string แบบสุ่ม
			String tokenString = generateRandomToken();

			// คำนวณวันหมดอายุ
			Calendar calendar = Calendar.getInstance();
			Date createdDate = calendar.getTime();
			calendar.add(Calendar.DAY_OF_YEAR, daysValid);
			Date expiryDate = calendar.getTime();

			// สร้าง DownloadToken object
			DownloadToken token = new DownloadToken(tokenString, fileId, createdDate, expiryDate);

			// บันทึกลง database
			session.save(token);
			session.getTransaction().commit();

			System.out.println("✅ Created download token: " + tokenString + " for file ID: " + fileId);
			return tokenString;

		} catch (Exception e) {
			session.getTransaction().rollback();
			e.printStackTrace();
			throw new RuntimeException("Failed to create download token", e);
		} finally {
			session.close();
		}
	}

	// ตรวจสอบ token
	public DownloadToken validateToken(String tokenString) {
		SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
		Session session = sessionFactory.openSession();

		try {
			Query<DownloadToken> query = session.createQuery("FROM DownloadToken WHERE tokenString = :token",
					DownloadToken.class);
			query.setParameter("token", tokenString);

			DownloadToken token = query.uniqueResult();

			if (token != null) {
				System.out.println("🔍 Token found - File ID: " + token.getFileId() + ", Expired: " + token.isExpired()
						+ ", Active: " + token.isActive());
			} else {
				System.out.println("❌ Token not found: " + tokenString);
			}

			return token;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			session.close();
		}
	}

	// บันทึกการดาวน์โหลด
	public void recordDownload(String tokenString) {
		SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
		Session session = sessionFactory.openSession();
		session.beginTransaction();

		try {
			Query<DownloadToken> query = session.createQuery("FROM DownloadToken WHERE tokenString = :token",
					DownloadToken.class);
			query.setParameter("token", tokenString);

			DownloadToken token = query.uniqueResult();
			if (token != null) {
				token.setDownloadCount(token.getDownloadCount() + 1);
				token.setLastDownloaded(new Date());
				session.merge(token);
				session.getTransaction().commit();

				System.out.println("📊 Recorded download - Count: " + token.getDownloadCount());
			}

		} catch (Exception e) {
			session.getTransaction().rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	// ลบ token ที่หมดอายุ (สำหรับ scheduled cleanup)
	public int cleanupExpiredTokens() {
		SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
		Session session = sessionFactory.openSession();
		session.beginTransaction();

		try {
			Query query = session.createQuery("DELETE FROM DownloadToken WHERE expiryDate < :now OR isActive = false");
			query.setParameter("now", new Date());

			int deletedCount = query.executeUpdate();
			session.getTransaction().commit();

			System.out.println("🧹 Cleaned up " + deletedCount + " expired tokens");
			return deletedCount;

		} catch (Exception e) {
			session.getTransaction().rollback();
			e.printStackTrace();
			return 0;
		} finally {
			session.close();
		}
	}

	// สร้าง random token
	private String generateRandomToken() {
		SecureRandom random = new SecureRandom();
		StringBuilder token = new StringBuilder(TOKEN_LENGTH);

		for (int i = 0; i < TOKEN_LENGTH; i++) {
			token.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
		}

		// เพิ่ม timestamp เพื่อความเป็นเอกลักษณ์
		String timestamp = String.valueOf(System.currentTimeMillis());
		String combined = token.toString() + timestamp;

		// encode เป็น Base64 แล้วตัดให้เหลือความยาวที่ต้องการ
		return Base64.getUrlEncoder().withoutPadding().encodeToString(combined.getBytes()).substring(0,
				Math.min(TOKEN_LENGTH, combined.length()));
	}
}