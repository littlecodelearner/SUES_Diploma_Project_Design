package sues.xmz.diploma.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sues.xmz.diploma.common.exception.health_profiles.*;
import sues.xmz.diploma.domain.dto.health_profiles.HealthProfileDTO;
import sues.xmz.diploma.domain.po.HealthProfiles;
import sues.xmz.diploma.domain.req.health_profiles.HealthProfileUpdateReq;
import sues.xmz.diploma.domain.req.health_profiles.HealthProfilesCreateReq;
import sues.xmz.diploma.mapper.HealthProfilesMapper;
import sues.xmz.diploma.service.HealthProfilesService;

@Service
@Transactional
public class HealthProfilesServiceImpl extends ServiceImpl<HealthProfilesMapper, HealthProfiles> implements HealthProfilesService{

    @Override
    public void createHealthProfile(HealthProfilesCreateReq healthProfilesCreateReq) {
        HealthProfiles checkHealthProfile = this.baseMapper.selectOne(
                new LambdaQueryWrapper<HealthProfiles>()
                        .eq(HealthProfiles::getUserId, healthProfilesCreateReq.getUserId())
        );
        if (checkHealthProfile != null) {
            throw new OnlyOneHealthProfilePerUserException();
        }

        boolean success = this.saveOrUpdate(BeanUtil.copyProperties(healthProfilesCreateReq, HealthProfiles.class));
        if (!success) {
            throw new HealthProfileCreateException();
        }
    }

    @Override
    public HealthProfileDTO getHealthProfileByUserId(Integer userId) {
        HealthProfiles healthProfile = this.baseMapper.selectOne(
                new LambdaQueryWrapper<HealthProfiles>()
                        .eq(HealthProfiles::getUserId, userId)
        );

        if (healthProfile == null) {
            throw new HealthProfileNotFoundException();
        }

        return BeanUtil.copyProperties(healthProfile, HealthProfileDTO.class);
    }

    @Override
    public void updateHealthProfile(HealthProfileUpdateReq healthProfileUpdateReq) {
        LambdaQueryWrapper<HealthProfiles> healthProfilesLambdaQueryWrapper =
                Wrappers.<HealthProfiles>lambdaQuery()
                        .eq(HealthProfiles::getUserId, healthProfileUpdateReq.getUserId());
        HealthProfiles healthProfile = this.getOne(healthProfilesLambdaQueryWrapper);

        if (healthProfile == null){
            throw new HealthProfileNotFoundException();
        }

        BeanUtils.copyProperties(healthProfileUpdateReq, healthProfile);
        boolean success = this.updateById(healthProfile);
        if (!success) {
            throw new HealthProfileUpdateException();
        }

    }

    @Override
    public void deleteHealthProfileByUserId(Integer userId) {
        HealthProfiles healthProfile = this.baseMapper.selectOne(
                new LambdaQueryWrapper<HealthProfiles>()
                .eq(HealthProfiles::getUserId, userId)
        );

        if (healthProfile == null) {
            throw new HealthProfileNotFoundException();
        }

        boolean success = this.removeById(healthProfile.getProfileId());
        if (!success) {
            throw new HealthProfileDeleteException();
        }
    }
}
