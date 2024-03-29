package com.gk.webmagic.demo3.mapper;

import com.gk.webmagic.demo3.model.NcpssdBean;
import com.gk.webmagic.demo3.model.NcpssdBeanExample;
import com.gk.webmagic.demo3.model.NcpssdBeanWithBLOBs;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface NcpssdBeanMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_ncpssd
     *
     * @mbg.generated
     */
    long countByExample(NcpssdBeanExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_ncpssd
     *
     * @mbg.generated
     */
    int deleteByExample(NcpssdBeanExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_ncpssd
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_ncpssd
     *
     * @mbg.generated
     */
    int insert(NcpssdBeanWithBLOBs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_ncpssd
     *
     * @mbg.generated
     */
    int insertSelective(NcpssdBeanWithBLOBs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_ncpssd
     *
     * @mbg.generated
     */
    List<NcpssdBeanWithBLOBs> selectByExampleWithBLOBs(NcpssdBeanExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_ncpssd
     *
     * @mbg.generated
     */
    List<NcpssdBean> selectByExample(NcpssdBeanExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_ncpssd
     *
     * @mbg.generated
     */
    NcpssdBeanWithBLOBs selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_ncpssd
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") NcpssdBeanWithBLOBs record, @Param("example") NcpssdBeanExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_ncpssd
     *
     * @mbg.generated
     */
    int updateByExampleWithBLOBs(@Param("record") NcpssdBeanWithBLOBs record, @Param("example") NcpssdBeanExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_ncpssd
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") NcpssdBean record, @Param("example") NcpssdBeanExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_ncpssd
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(NcpssdBeanWithBLOBs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_ncpssd
     *
     * @mbg.generated
     */
    int updateByPrimaryKeyWithBLOBs(NcpssdBeanWithBLOBs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_ncpssd
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(NcpssdBean record);
}