package sues.xmz.diploma.service;

import com.baomidou.mybatisplus.extension.service.IService;
import sues.xmz.diploma.domain.dto.health_profiles.HealthProfileDTO;
import sues.xmz.diploma.domain.po.HealthProfiles;
import sues.xmz.diploma.domain.req.health_profiles.HealthProfileUpdateReq;
import sues.xmz.diploma.domain.req.health_profiles.HealthProfilesCreateReq;
public interface HealthProfilesService extends IService<HealthProfiles>{

    void createHealthProfile(HealthProfilesCreateReq healthProfilesCreateReq);

    HealthProfileDTO getHealthProfileByUserId(Integer userId);

    void updateHealthProfile(HealthProfileUpdateReq healthProfileUpdateReq);

    void deleteHealthProfileByUserId(Integer userId);

}
