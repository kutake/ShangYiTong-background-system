package com.atguigu.common.system.service;

import com.atguigu.common.system.model.SysRole;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
public class SysRoleServiceTest {

    @Resource
    private SysRoleService sysRoleService;

    @Test
    public void testSelectList() {
        System.out.println(("----- selectAll method test ------"));
        //UserMapper 中的 selectList() 方法的参数为 MP 内置的条件封装器 Wrapper
        //所以不填写就是无任何条件
        List<SysRole> sysRoles = sysRoleService.list();
        for (SysRole role : sysRoles) {
            System.out.println("role = " + role);
        }
    }
}
