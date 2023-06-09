package com.atguigu.syt.hosp.service;

import com.atguigu.syt.model.hosp.Schedule;
import com.atguigu.syt.vo.hosp.ScheduleOrderVo;
import com.atguigu.syt.vo.order.OrderMqVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ScheduleService {
    /**
     *  根据医院唯一标识,排班编号 更新/保存 排班信息
     * @author liuzhaoxu
     * @date 2023/6/7 20:32
     * @param paramMap
     * @param hoscode
     * @param hosScheduleId
     */

    void save(Map<String, Object> paramMap, String hoscode, String hosScheduleId);

    /**
     *  根据医院标识获取排班信息
     * @author liuzhaoxu
     * @date 2023/6/7 21:20
     * @param page
     * @param limit
     * @param hoscode
     * @return org.springframework.data.domain.Page<com.atguigu.syt.model.hosp.Schedule>
     */

    Page<Schedule> listSchedulePage(Integer page, Integer limit, String hoscode);

    /**
     *  根据医院标识和排班id删除排班信息
     * @author liuzhaoxu
     * @date 2023/6/8 10:44
     * @param paramMap
     */

    void deleteSchedule(Map<String, Object> paramMap);

    /**
     *  获取小科室的排班日期分页以及计算周几
     * @author liuzhaoxu
     * @date 2023/6/11 18:02
     * @param page
     * @param limit
     * @param hoscode
     * @param depcode
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */

    Map<String, Object> getSchedulePage(Long page, Long limit, String hoscode, String depcode);


    /**
     *  根据医院,科室,日期获取当天排班信息
     * @author liuzhaoxu
     * @date 2023/6/11 19:33
     * @param hoscode
     * @param depcode
     * @param workDate
     * @return java.util.List<com.atguigu.syt.model.hosp.Schedule>
     */

    List<Schedule> listSchedule(String hoscode, String depcode, String workDate);

    /**
     *  获取挂号周期信息
     * @author liuzhaoxu
     * @date 2023/6/19 8:50
     * @param hoscode
     * @param depcode
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    Map<String, Object> getScheduleCycleInfo(String hoscode, String depcode);


    /**
     *  获取预约详情
     * @author liuzhaoxu
     * @date 2023/6/19 18:13
     * @param id
     * @return com.atguigu.syt.model.hosp.Schedule
     */

    Schedule getDetailById(String id);

    /**
     *  获取排版信息
     * @author liuzhaoxu
     * @date 2023/6/19 20:12
     * @param scheduleId
     * @return com.atguigu.syt.vo.hosp.ScheduleOrderVo
     */

    ScheduleOrderVo getScheduleOrderVo(String scheduleId);

    /**
     *  根据mq消息，修改排班信息
     * @author liuzhaoxu
     * @date 2023/6/20 19:42
     * @param orderMqVo
     */

    void updateByOrderMqVo(OrderMqVo orderMqVo);
}
