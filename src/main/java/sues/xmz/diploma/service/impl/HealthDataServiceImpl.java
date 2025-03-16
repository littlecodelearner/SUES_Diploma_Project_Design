package sues.xmz.diploma.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sues.xmz.diploma.common.domain.PageResponse;
import sues.xmz.diploma.common.exception.health_data.HealthDataNotFoundException;
import sues.xmz.diploma.common.exception.health_data.UserHeightDataNotFoundException;
import sues.xmz.diploma.common.utils.ChartGenerateUtil;
import sues.xmz.diploma.common.utils.PageUtil;
import sues.xmz.diploma.common.utils.UserDataCalculationUtil;
import sues.xmz.diploma.domain.dto.health_data.HealthDataDTO;
import sues.xmz.diploma.domain.dto.health_data.HealthDataTrendDTO;
import sues.xmz.diploma.domain.po.HealthData;
import sues.xmz.diploma.domain.po.Users;
import sues.xmz.diploma.domain.req.health_data.HealthDataCreateReq;
import sues.xmz.diploma.domain.req.health_data.HealthDataQueryReq;
import sues.xmz.diploma.domain.req.health_data.HealthDataUpdateReq;
import sues.xmz.diploma.mapper.HealthDataMapper;
import sues.xmz.diploma.service.HealthDataService;
import sues.xmz.diploma.service.UsersService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
public class HealthDataServiceImpl extends ServiceImpl<HealthDataMapper, HealthData> implements HealthDataService {

    @Resource
    private UsersService usersService;

    @Override
    public boolean addHealthDataInBatch(List<HealthDataCreateReq> healthDataCreateReqList) {
        //计算BMI并进行赋值
        List<HealthData> healthDataList = getHealDataListCalculatedBmi(BeanUtil.copyToList(healthDataCreateReqList, HealthData.class));

        // 保存数据并且返回结果
        return this.saveBatch(healthDataList);
    }

    /**
     * 计算BMI并进行赋值
     *
     * @param healthDataList 未含有BMI值的健康数据列表
     *
     * @return 含有BMI值的健康数据列表
     */
    private List<HealthData> getHealDataListCalculatedBmi(List<HealthData> healthDataList) {
        Users user = usersService.getById(healthDataList.get(0).getUserId());
        if (user.getHeight() == null || user.getHeight().compareTo(BigDecimal.ZERO) <= 0) {
            throw new UserHeightDataNotFoundException();
        }

        return healthDataList.stream()
                .peek(healthData -> {
                    BigDecimal bmi = UserDataCalculationUtil.calculateBMI(user.getHeight(), healthData.getWeight());
                    healthData.setBmi(bmi);
                })
                .toList();
    }

    @Override
    public boolean updateHealthDataInBatch(List<HealthDataUpdateReq> healthDataUpdateReqList) {
        //计算BMI并进行赋值
        List<HealthData> healthDataList = getHealDataListCalculatedBmi(BeanUtil.copyToList(healthDataUpdateReqList, HealthData.class));

        // 更新数据并且返回结果
        return this.updateBatchById(healthDataList);
    }

    @Override
    public boolean deleteHealthData(List<Integer> healthDataIdList) {
        return removeBatchByIds(healthDataIdList);
    }

    @Override
    public PageResponse<HealthDataDTO> listPaginatedHealthDataByTimeRange(HealthDataQueryReq healthDataQueryReq) {
        PageUtil.pageParamIfNull(healthDataQueryReq.getCurrent(), healthDataQueryReq.getSize());
        List<HealthDataDTO> healthDataDTOList = baseMapper.listPaginatedHealthDataByTimeRange(
                healthDataQueryReq,
                PageUtil.countOffset(healthDataQueryReq.getCurrent(), healthDataQueryReq.getSize())
        );
        return PageResponse.getPageResponse(
                healthDataDTOList,
                healthDataQueryReq.getCurrent(),
                healthDataQueryReq.getSize(),
                baseMapper.listAllHealthDataByTimeRange(healthDataQueryReq).size()
        );
    }

    @Override
    public byte[] getHealthDataTrend(HealthDataQueryReq healthDataQueryReq) throws IOException {
        PageUtil.pageParamIfNull(healthDataQueryReq.getCurrent(), healthDataQueryReq.getSize());
        // 根据用户ID和时间范围查询
        List<HealthDataDTO> healthDataDTOList = baseMapper.listPaginatedHealthDataByTimeRange(
                healthDataQueryReq,
                PageUtil.countOffset(healthDataQueryReq.getCurrent(), healthDataQueryReq.getSize())
        );
        if (healthDataDTOList == null || healthDataDTOList.isEmpty()) {
            throw new HealthDataNotFoundException();
        }

        // 汇总数据，生成趋势DTO
        HealthDataTrendDTO healthDataTrendDTO = new HealthDataTrendDTO();

        healthDataTrendDTO.setBmis(
                healthDataDTOList.parallelStream()
                        .map(HealthDataDTO::getBmi)
                        .collect(Collectors.toList())
        );

        healthDataTrendDTO.setMeasurementDates(healthDataDTOList.parallelStream()
                .map(HealthDataDTO::getMeasurementDate)
                .collect(Collectors.toList()));

        healthDataTrendDTO.setHeartRates(healthDataDTOList.parallelStream()
                .map(HealthDataDTO::getHeartRate)
                .collect(Collectors.toList()));

        healthDataTrendDTO.setWeights(healthDataDTOList.parallelStream()
                .map(HealthDataDTO::getWeight)
                .collect(Collectors.toList()));

        //绘画趋势图 - 方法1
//        List<byte[]> byteArrayChartList = generateTendencyChartOfHealthData(healthDataTrendDTO);

        //绘画趋势图 - 方法2
        return ChartGenerateUtil.generateCombinedTrendChart(healthDataTrendDTO);
    }

}
