package com.gk.webmagic.demo2.model;

/**
 *
 * This class was generated by MyBatis Generator.
 * This class corresponds to the database table down_peri_cata_lst
 *
 * @mbg.generated do_not_delete_during_merge
 */
public class DownPeriCataLstWithBLOBs extends DownPeriCataLst{
    /**
     * Database Column Remarks:
     *   摘要
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column down_peri_cata_lst.PERI_ABSTRACT
     *
     * @mbg.generated
     */
    private String periAbstract;

    /**
     * Database Column Remarks:
     *   页面显示内容
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column down_peri_cata_lst.DOC_FIELD
     *
     * @mbg.generated
     */
    private String docField;

    /**
     * Database Column Remarks:
     *   MARC内容
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column down_peri_cata_lst.MARC_DATA
     *
     * @mbg.generated
     */
    private String marcData;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column down_peri_cata_lst.PERI_ABSTRACT
     *
     * @return the value of down_peri_cata_lst.PERI_ABSTRACT
     *
     * @mbg.generated
     */
    public String getPeriAbstract() {
        return periAbstract;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column down_peri_cata_lst.PERI_ABSTRACT
     *
     * @param periAbstract the value for down_peri_cata_lst.PERI_ABSTRACT
     *
     * @mbg.generated
     */
    public void setPeriAbstract(String periAbstract) {
        this.periAbstract = periAbstract == null ? null : periAbstract.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column down_peri_cata_lst.DOC_FIELD
     *
     * @return the value of down_peri_cata_lst.DOC_FIELD
     *
     * @mbg.generated
     */
    public String getDocField() {
        return docField;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column down_peri_cata_lst.DOC_FIELD
     *
     * @param docField the value for down_peri_cata_lst.DOC_FIELD
     *
     * @mbg.generated
     */
    public void setDocField(String docField) {
        this.docField = docField == null ? null : docField.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column down_peri_cata_lst.MARC_DATA
     *
     * @return the value of down_peri_cata_lst.MARC_DATA
     *
     * @mbg.generated
     */
    public String getMarcData() {
        return marcData;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column down_peri_cata_lst.MARC_DATA
     *
     * @param marcData the value for down_peri_cata_lst.MARC_DATA
     *
     * @mbg.generated
     */
    public void setMarcData(String marcData) {
        this.marcData = marcData == null ? null : marcData.trim();
    }
}