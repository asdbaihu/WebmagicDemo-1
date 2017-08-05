package com.gk.webmagic.demo2.model;

/**
 *
 * This class was generated by MyBatis Generator.
 * This class corresponds to the database table t_wanfang_journal_compare
 *
 * @mbg.generated do_not_delete_during_merge
 */
public class WanFangJournalCompare {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_wanfang_journal_compare.id
     *
     * @mbg.generated
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_wanfang_journal_compare.journal_name
     *
     * @mbg.generated
     */
    private String journalName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_wanfang_journal_compare.journal_url
     *
     * @mbg.generated
     */
    private String journalUrl;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_wanfang_journal_compare.doc_id
     *
     * @mbg.generated
     */
    private String docId;

    /**
     * Database Column Remarks:
     *   比对成功flag=1，比对失败flag=0
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_wanfang_journal_compare.success_flag
     *
     * @mbg.generated
     */
    private String successFlag;

    /**
     * Database Column Remarks:
     *   备注字段
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_wanfang_journal_compare.remark
     *
     * @mbg.generated
     */
    private String remark;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_wanfang_journal_compare.id
     *
     * @return the value of t_wanfang_journal_compare.id
     *
     * @mbg.generated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_wanfang_journal_compare.id
     *
     * @param id the value for t_wanfang_journal_compare.id
     *
     * @mbg.generated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_wanfang_journal_compare.journal_name
     *
     * @return the value of t_wanfang_journal_compare.journal_name
     *
     * @mbg.generated
     */
    public String getJournalName() {
        return journalName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_wanfang_journal_compare.journal_name
     *
     * @param journalName the value for t_wanfang_journal_compare.journal_name
     *
     * @mbg.generated
     */
    public void setJournalName(String journalName) {
        this.journalName = journalName == null ? null : journalName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_wanfang_journal_compare.journal_url
     *
     * @return the value of t_wanfang_journal_compare.journal_url
     *
     * @mbg.generated
     */
    public String getJournalUrl() {
        return journalUrl;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_wanfang_journal_compare.journal_url
     *
     * @param journalUrl the value for t_wanfang_journal_compare.journal_url
     *
     * @mbg.generated
     */
    public void setJournalUrl(String journalUrl) {
        this.journalUrl = journalUrl == null ? null : journalUrl.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_wanfang_journal_compare.doc_id
     *
     * @return the value of t_wanfang_journal_compare.doc_id
     *
     * @mbg.generated
     */
    public String getDocId() {
        return docId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_wanfang_journal_compare.doc_id
     *
     * @param docId the value for t_wanfang_journal_compare.doc_id
     *
     * @mbg.generated
     */
    public void setDocId(String docId) {
        this.docId = docId == null ? null : docId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_wanfang_journal_compare.success_flag
     *
     * @return the value of t_wanfang_journal_compare.success_flag
     *
     * @mbg.generated
     */
    public String getSuccessFlag() {
        return successFlag;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_wanfang_journal_compare.success_flag
     *
     * @param successFlag the value for t_wanfang_journal_compare.success_flag
     *
     * @mbg.generated
     */
    public void setSuccessFlag(String successFlag) {
        this.successFlag = successFlag == null ? null : successFlag.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_wanfang_journal_compare.remark
     *
     * @return the value of t_wanfang_journal_compare.remark
     *
     * @mbg.generated
     */
    public String getRemark() {
        return remark;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_wanfang_journal_compare.remark
     *
     * @param remark the value for t_wanfang_journal_compare.remark
     *
     * @mbg.generated
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }
}