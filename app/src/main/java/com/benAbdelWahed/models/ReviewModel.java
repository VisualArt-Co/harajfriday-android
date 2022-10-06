package com.benAbdelWahed.models;

public class ReviewModel {
  private String comment;
  private boolean isDealedWith,isLikeHim;

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public boolean isDealedWith() {
    return isDealedWith;
  }

  public void setDealedWith(boolean dealedWith) {
    isDealedWith = dealedWith;
  }

  public boolean isLikeHim() {
    return isLikeHim;
  }

  public void setLikeHim(boolean likeHim) {
    isLikeHim = likeHim;
  }
}
