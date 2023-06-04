package com.atguigu.syt.cmn.service;

import com.atguigu.syt.model.cmn.Region;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  地区管理服务类
 * </p>
 *
 * @author atguigu
 * @since 2023-06-04
 */
public interface RegionService extends IService<Region> {

    /**
     *  根据上级节点获取下一级别地区信息
     * @author liuzhaoxu
     * @date 2023/6/4 19:56
     * @param parentCode
     * @return java.util.List<com.atguigu.syt.model.cmn.Region>
     */

    List<Region> findRegionListByParentCode(String parentCode);

}