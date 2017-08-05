package com.gk.webmagic.demo2.mapper;

import com.gk.webmagic.demo2.model.DownPeriVolLst;
import com.gk.webmagic.demo2.model.DownPeriVolLstExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DownPeriVolLstMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_peri_vol_lst
     *
     * @mbg.generated
     */
    long countByExample(DownPeriVolLstExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_peri_vol_lst
     *
     * @mbg.generated
     */
    int deleteByExample(DownPeriVolLstExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_peri_vol_lst
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer periVolId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_peri_vol_lst
     *
     * @mbg.generated
     */
    int insert(DownPeriVolLst record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_peri_vol_lst
     *
     * @mbg.generated
     */
    int insertSelective(DownPeriVolLst record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_peri_vol_lst
     *
     * @mbg.generated
     */
    List<DownPeriVolLst> selectByExampleWithBLOBs(DownPeriVolLstExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_peri_vol_lst
     *
     * @mbg.generated
     */
    List<DownPeriVolLst> selectByExample(DownPeriVolLstExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_peri_vol_lst
     *
     * @mbg.generated
     */
    DownPeriVolLst selectByPrimaryKey(Integer periVolId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_peri_vol_lst
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") DownPeriVolLst record, @Param("example") DownPeriVolLstExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_peri_vol_lst
     *
     * @mbg.generated
     */
    int updateByExampleWithBLOBs(@Param("record") DownPeriVolLst record, @Param("example") DownPeriVolLstExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_peri_vol_lst
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") DownPeriVolLst record, @Param("example") DownPeriVolLstExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_peri_vol_lst
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(DownPeriVolLst record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_peri_vol_lst
     *
     * @mbg.generated
     */
    int updateByPrimaryKeyWithBLOBs(DownPeriVolLst record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_peri_vol_lst
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(DownPeriVolLst record);
}