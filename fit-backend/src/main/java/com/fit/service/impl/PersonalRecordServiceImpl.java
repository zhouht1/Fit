package com.fit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fit.entity.PersonalRecord;
import com.fit.mapper.PersonalRecordMapper;
import com.fit.service.PersonalRecordService;
import org.springframework.stereotype.Service;

@Service
public class PersonalRecordServiceImpl extends ServiceImpl<PersonalRecordMapper, PersonalRecord> implements PersonalRecordService {
}