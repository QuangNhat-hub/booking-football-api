package com.booking.football_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


import java.time.LocalDateTime;

@Entity
@Table(name="REVIEW")
public class Review {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "Review_id")
	    private Integer reviewId;

	    @Column(name = "Booking_id")
	    private Integer bookingId;

	    @Column(name = "User_id")
	    private Integer userId;

	    @Column(name = "Field_id")
	    private Integer fieldId;

	    @Column(name = "Rating")
	    private Integer rating;

	    @Column(name = "Comment", columnDefinition = "NVARCHAR(MAX)")
	    private String comment;

	    @Column(name = "Image_url")
	    private String imageUrl;

	    @Column(name = "Review_date")
	    private LocalDateTime reviewDate;

	    @Column(name = "Updated_at")
	    private LocalDateTime updatedAt;

	    @Column(name = "Status")
	    private Boolean status = true;

		public Review() {
			super();
		}

		public Review(Integer reviewId, Integer bookingId, Integer userId, Integer fieldId, Integer rating,
				String comment, String imageUrl, LocalDateTime reviewDate, LocalDateTime updatedAt, Boolean status) {
			super();
			this.reviewId = reviewId;
			this.bookingId = bookingId;
			this.userId = userId;
			this.fieldId = fieldId;
			this.rating = rating;
			this.comment = comment;
			this.imageUrl = imageUrl;
			this.reviewDate = reviewDate;
			this.updatedAt = updatedAt;
			this.status = status;
		}

		public Integer getReviewId() {
			return reviewId;
		}

		public void setReviewId(Integer reviewId) {
			this.reviewId = reviewId;
		}

		public Integer getBookingId() {
			return bookingId;
		}

		public void setBookingId(Integer bookingId) {
			this.bookingId = bookingId;
		}

		public Integer getUserId() {
			return userId;
		}

		public void setUserId(Integer userId) {
			this.userId = userId;
		}

		public Integer getFieldId() {
			return fieldId;
		}

		public void setFieldId(Integer fieldId) {
			this.fieldId = fieldId;
		}

		public Integer getRating() {
			return rating;
		}

		public void setRating(Integer rating) {
			this.rating = rating;
		}

		public String getComment() {
			return comment;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}

		public String getImageUrl() {
			return imageUrl;
		}

		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}

		public LocalDateTime getReviewDate() {
			return reviewDate;
		}

		public void setReviewDate(LocalDateTime reviewDate) {
			this.reviewDate = reviewDate;
		}

		public LocalDateTime getUpdatedAt() {
			return updatedAt;
		}

		public void setUpdatedAt(LocalDateTime updatedAt) {
			this.updatedAt = updatedAt;
		}

		public Boolean getStatus() {
			return status;
		}
        public void setStatus(Boolean status) {
			this.status = status;
		}
}
    