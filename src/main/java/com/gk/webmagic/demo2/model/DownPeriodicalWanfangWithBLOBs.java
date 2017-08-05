package com.gk.webmagic.demo2.model;

/**
 *
 * This class was generated by MyBatis Generator.
 * This class corresponds to the database table down_periodical_wanfang
 *
 * @mbg.generated do_not_delete_during_merge
 */
public class DownPeriodicalWanfangWithBLOBs extends DownPeriodicalWanfang {
    /**
     * Database Column Remarks:
     *   作者简介
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column down_periodical_wanfang.ABOUT_AUTHOR
     *
     * @mbg.generated
     */
    private String aboutAuthor;

    /**
     * Database Column Remarks:
     *   摘要
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column down_periodical_wanfang.PERI_ABSTRACT
     *
     * @mbg.generated
     */
    private String periAbstract;

    /**
     * Database Column Remarks:
     *   注释
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column down_periodical_wanfang.REMARK
     *
     * @mbg.generated
     */
    private String remark;

    /**
     * Database Column Remarks:
     *   评述项
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column down_periodical_wanfang.COMMENTS
     *
     * @mbg.generated
     */
    private String comments;

    /**
     * Database Column Remarks:
     *   引用参考文献
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column down_periodical_wanfang.REFERENCE
     *
     * @mbg.generated
     */
    private String reference;

    /**
     * Database Column Remarks:
     *   页面显示内容
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column down_periodical_wanfang.DOC_FIELD
     *
     * @mbg.generated
     */
    private String docField;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column down_periodical_wanfang.ABOUT_AUTHOR
     *
     * @return the value of down_periodical_wanfang.ABOUT_AUTHOR
     *
     * @mbg.generated
     */
    public String getAboutAuthor() {
        return aboutAuthor;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column down_periodical_wanfang.ABOUT_AUTHOR
     *
     * @param aboutAuthor the value for down_periodical_wanfang.ABOUT_AUTHOR
     *
     * @mbg.generated
     */
    public void setAboutAuthor(String aboutAuthor) {
        this.aboutAuthor = aboutAuthor == null ? null : aboutAuthor.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column down_periodical_wanfang.PERI_ABSTRACT
     *
     * @return the value of down_periodical_wanfang.PERI_ABSTRACT
     *
     * @mbg.generated
     */
    public String getPeriAbstract() {
        return periAbstract;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column down_periodical_wanfang.PERI_ABSTRACT
     *
     * @param periAbstract the value for down_periodical_wanfang.PERI_ABSTRACT
     *
     * @mbg.generated
     */
    public void setPeriAbstract(String periAbstract) {
        this.periAbstract = periAbstract == null ? null : periAbstract.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column down_periodical_wanfang.REMARK
     *
     * @return the value of down_periodical_wanfang.REMARK
     *
     * @mbg.generated
     */
    public String getRemark() {
        return remark;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column down_periodical_wanfang.REMARK
     *
     * @param remark the value for down_periodical_wanfang.REMARK
     *
     * @mbg.generated
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column down_periodical_wanfang.COMMENTS
     *
     * @return the value of down_periodical_wanfang.COMMENTS
     *
     * @mbg.generated
     */
    public String getComments() {
        return comments;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column down_periodical_wanfang.COMMENTS
     *
     * @param comments the value for down_periodical_wanfang.COMMENTS
     *
     * @mbg.generated
     */
    public void setComments(String comments) {
        this.comments = comments == null ? null : comments.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column down_periodical_wanfang.REFERENCE
     *
     * @return the value of down_periodical_wanfang.REFERENCE
     *
     * @mbg.generated
     */
    public String getReference() {
        return reference;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column down_periodical_wanfang.REFERENCE
     *
     * @param reference the value for down_periodical_wanfang.REFERENCE
     *
     * @mbg.generated
     */
    public void setReference(String reference) {
        this.reference = reference == null ? null : reference.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column down_periodical_wanfang.DOC_FIELD
     *
     * @return the value of down_periodical_wanfang.DOC_FIELD
     *
     * @mbg.generated
     */
    public String getDocField() {
        return docField;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column down_periodical_wanfang.DOC_FIELD
     *
     * @param docField the value for down_periodical_wanfang.DOC_FIELD
     *
     * @mbg.generated
     */
    public void setDocField(String docField) {
        this.docField = docField == null ? null : docField.trim();
    }
}