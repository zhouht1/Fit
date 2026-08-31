package com.fit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fit.entity.User;
import com.fit.mapper.UserMapper;
import com.fit.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}