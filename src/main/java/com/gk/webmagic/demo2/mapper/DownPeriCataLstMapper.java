package com.gk.webmagic.demo2.mapper;

import com.gk.webmagic.demo2.model.DownPeriCataLst;
import com.gk.webmagic.demo2.model.DownPeriCataLstExample;
import com.gk.webmagic.demo2.model.DownPeriCataLstWithBLOBs;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DownPeriCataLstMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_peri_cata_lst
     *
     * @mbg.generated
     */
    long countByExample(DownPeriCataLstExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_peri_cata_lst
     *
     * @mbg.generated
     */
    int deleteByExample(DownPeriCataLstExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_peri_cata_lst
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_peri_cata_lst
     *
     * @mbg.generated
     */
    int insert(DownPeriCataLstWithBLOBs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_peri_cata_lst
     *
     * @mbg.generated
     */
    int insertSelective(DownPeriCataLstWithBLOBs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_peri_cata_lst
     *
     * @mbg.generated
     */
    List<DownPeriCataLstWithBLOBs> selectByExampleWithBLOBs(DownPeriCataLstExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_peri_cata_lst
     *
     * @mbg.generated
     */
    List<DownPeriCataLst> selectByExample(DownPeriCataLstExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_peri_cata_lst
     *
     * @mbg.generated
     */
    DownPeriCataLstWithBLOBs selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_peri_cata_lst
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") DownPeriCataLstWithBLOBs record, @Param("example") DownPeriCataLstExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_peri_cata_lst
     *
     * @mbg.generated
     */
    int updateByExampleWithBLOBs(@Param("record") DownPeriCataLstWithBLOBs record, @Param("example") DownPeriCataLstExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_peri_cata_lst
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") DownPeriCataLst record, @Param("example") DownPeriCataLstExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_peri_cata_lst
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(DownPeriCataLstWithBLOBs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_peri_cata_lst
     *
     * @mbg.generated
     */
    int updateByPrimaryKeyWithBLOBs(DownPeriCataLstWithBLOBs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_peri_cata_lst
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(DownPeriCataLst record);
}