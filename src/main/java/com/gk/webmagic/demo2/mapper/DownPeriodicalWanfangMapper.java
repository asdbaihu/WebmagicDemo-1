package com.gk.webmagic.demo2.mapper;

import com.gk.webmagic.demo2.model.DownPeriodicalWanfang;
import com.gk.webmagic.demo2.model.DownPeriodicalWanfangExample;
import com.gk.webmagic.demo2.model.DownPeriodicalWanfangWithBLOBs;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DownPeriodicalWanfangMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_periodical_wanfang
     *
     * @mbg.generated
     */
    long countByExample(DownPeriodicalWanfangExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_periodical_wanfang
     *
     * @mbg.generated
     */
    int deleteByExample(DownPeriodicalWanfangExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_periodical_wanfang
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_periodical_wanfang
     *
     * @mbg.generated
     */
    int insert(DownPeriodicalWanfangWithBLOBs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_periodical_wanfang
     *
     * @mbg.generated
     */
    int insertSelective(DownPeriodicalWanfangWithBLOBs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_periodical_wanfang
     *
     * @mbg.generated
     */
    List<DownPeriodicalWanfangWithBLOBs> selectByExampleWithBLOBs(DownPeriodicalWanfangExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_periodical_wanfang
     *
     * @mbg.generated
     */
    List<DownPeriodicalWanfang> selectByExample(DownPeriodicalWanfangExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_periodical_wanfang
     *
     * @mbg.generated
     */
    DownPeriodicalWanfangWithBLOBs selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_periodical_wanfang
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") DownPeriodicalWanfangWithBLOBs record, @Param("example") DownPeriodicalWanfangExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_periodical_wanfang
     *
     * @mbg.generated
     */
    int updateByExampleWithBLOBs(@Param("record") DownPeriodicalWanfangWithBLOBs record, @Param("example") DownPeriodicalWanfangExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_periodical_wanfang
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") DownPeriodicalWanfang record, @Param("example") DownPeriodicalWanfangExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_periodical_wanfang
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(DownPeriodicalWanfangWithBLOBs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_periodical_wanfang
     *
     * @mbg.generated
     */
    int updateByPrimaryKeyWithBLOBs(DownPeriodicalWanfangWithBLOBs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table down_periodical_wanfang
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(DownPeriodicalWanfang record);
    
    /**
     * 删除爬取比较未完成的数据
     * @param remark
     * @return
     */
    int deleteUnfinishedData(String remark);
}