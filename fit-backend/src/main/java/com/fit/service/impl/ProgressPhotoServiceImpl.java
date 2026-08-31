package com.fit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fit.entity.ProgressPhoto;
import com.fit.mapper.ProgressPhotoMapper;
import com.fit.service.ProgressPhotoService;
import org.springframework.stereotype.Service;

@Service
public class ProgressPhotoServiceImpl extends ServiceImpl<ProgressPhotoMapper, ProgressPhoto> implements ProgressPhotoService {
}