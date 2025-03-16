package sues.xmz.diploma.common.utils;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import sues.xmz.diploma.domain.dto.health_data.HealthDataTrendDTO;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 提供生成健康数据趋势图表的工具方法。
 * <p>
 * 该类包含生成单个趋势图和综合趋势图的方法，并支持数据采样以优化性能。
 */
public class ChartGenerateUtil {

    /**
     * 生成单个健康指标的趋势图，并返回PNG格式的图像字节数组。
     * <p>
     * 该方法根据提供的日期和值数据生成时间序列图表。
     *
     * @param dates      数据对应的日期列表
     * @param values     数据对应的值列表
     * @param title      图表标题
     * @param yAxisLabel Y轴标签
     *
     * @return 图像的字节数组
     *
     * @throws IOException 如果图像生成过程中发生I/O错误
     */
    public static byte[] generateTrendChart(List<Date> dates, List<BigDecimal> values, String title, String yAxisLabel) throws IOException {
        // 创建时间序列
        TimeSeries series = new TimeSeries(title);
        for (int i = 0; i < dates.size(); i++) {
            Date date = dates.get(i);
            BigDecimal value = values.get(i);
            series.add(new Day(date), value.doubleValue());
        }
        // 创建数据集
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(series);
        // 创建图表
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                title,
                "Date",
                yAxisLabel,
                dataset,
                true,
                true,
                false
        );
        // 渲染成PNG图像
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(byteArrayOutputStream, chart, 800, 600);
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * 生成包含BMI、心率和体重趋势的综合图表，并返回PNG格式的图像字节数组。
     * <p>
     * 注意事项：
     * <p>
     * 1. 处理时间戳精度
     * <p>
     * 根据数据的时间精度选择合适的时间戳类，例如Day、Minute或Millisecond。在本例中，我们使用Day来处理按天采集的数据。
     * <p>
     * 2. 处理空值和异常
     * <p>
     * 在数据添加过程中，检查空值并跳过无效数据点。通过抛出IllegalArgumentException来处理数据不一致的情况。
     * <p>
     * 3. 性能优化 - 数据采样
     * <p>
     * 为了优化性能，可以对数据进行采样，减少数据点数量。例如，选择每隔一定时间间隔的数据点：
     * <pre>
     * 示例：选择每隔7天的数据点
     * <p>
     * {@code
     * for (int i = 0; i < dto.getMeasurementDates().size(); i += 7) {
     *  Date date = dto.getMeasurementDates().get(i);
     *  if (date == null) {
     *      continue;
     *  }
     *  // 添加数据点到相应的系列
     *  // ...
     * }
     *
     * }
     * <pre>
     * @param dto 包含健康数据的DTO对象
     *
     * @return 图像的字节数组
     *
     * @throws IOException 如果图像生成过程中发生I/O错误
     */
    public static byte[] generateCombinedTrendChart(HealthDataTrendDTO dto) throws IOException {
        // 检查数据的有效性
        if (dto.getBmis() == null || dto.getHeartRates() == null || dto.getWeights() == null || dto.getMeasurementDates() == null) {
            throw new IllegalArgumentException("数据列表不能为空");
        }
        if (dto.getBmis().size() != dto.getHeartRates().size() || dto.getBmis().size() != dto.getWeights().size() || dto.getBmis().size() != dto.getMeasurementDates().size()) {
            throw new IllegalArgumentException("数据列表长度不一致");
        }

        // 创建三个TimeSeries
        TimeSeries bmiSeries = new TimeSeries("BMI");
        TimeSeries heartRateSeries = new TimeSeries("Heart Rate");
        TimeSeries weightSeries = new TimeSeries("Weight");

        for (int i = 0; i < dto.getMeasurementDates().size(); i++) {
            Date date = dto.getMeasurementDates().get(i);
            if (date == null) {
                continue;
            }
            // 添加BMI数据
            BigDecimal bmi = dto.getBmis().get(i);
            if (bmi != null) {
                bmiSeries.addOrUpdate(new Day(date), bmi.doubleValue());
            }
            // 添加心率数据
            BigDecimal heartRate = dto.getHeartRates().get(i);
            if (heartRate != null) {
                heartRateSeries.addOrUpdate(new Day(date), heartRate.doubleValue());
            }
            // 添加体重数据
            BigDecimal weight = dto.getWeights().get(i);
            if (weight != null) {
                weightSeries.addOrUpdate(new Day(date), weight.doubleValue());
            }
        }

        // 创建三个独立的TimeSeriesCollection
        TimeSeriesCollection bmiDataset = new TimeSeriesCollection(bmiSeries);
        TimeSeriesCollection heartRateDataset = new TimeSeriesCollection(heartRateSeries);
        TimeSeriesCollection weightDataset = new TimeSeriesCollection(weightSeries);

        // 创建主Y轴和Date轴
        DateAxis dateAxis = new DateAxis("Date");
        NumberAxis bmiAxis = new NumberAxis("BMI");

        // 创建XYPlot并设置主数据集和轴
        XYPlot plot = new XYPlot(bmiDataset, dateAxis, bmiAxis, new XYLineAndShapeRenderer());

        // 添加第二个数据集和Y轴
        plot.setDataset(1, heartRateDataset);
        plot.setRangeAxis(1, new NumberAxis("Heart Rate"));

        // 添加第三个数据集和Y轴
        plot.setDataset(2, weightDataset);
        plot.setRangeAxis(2, new NumberAxis("Weight"));

        // 创建图表
        JFreeChart chart = new JFreeChart("Health Trends Over Time", JFreeChart.DEFAULT_TITLE_FONT, plot, true);

        // 渲染图表为PNG图像
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(byteArrayOutputStream, chart, 800, 600);
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * 生成包含BMI、心率和体重趋势的综合图表，并返回PNG格式的图像字节数组，采用数据采样优化性能。
     * <p>
     * 该方法对数据进行采样以减少数据点数量，提高图表生成性能，并生成包含三个指标的综合趋势图。
     *
     * @param dto 包含健康数据的DTO对象
     *
     * @return 图像的字节数组
     *
     * @throws IOException 如果图像生成过程中发生I/O错误
     */
    public static byte[] generateCombinedTrendChartOptimized(HealthDataTrendDTO dto) throws IOException {
        // 数据采样
        int maxPoints = 1000;
        List<Date> sampledDates = sampleData(dto.getMeasurementDates(), maxPoints);
        List<BigDecimal> sampledBMIs = sampleData(dto.getBmis(), maxPoints);
        List<BigDecimal> sampledHeartRates = sampleData(dto.getHeartRates(), maxPoints);
        List<BigDecimal> sampledWeights = sampleData(dto.getWeights(), maxPoints);

        // 创建TimeSeries并填充数据
        TimeSeries bmiSeries = new TimeSeries("BMI");
        TimeSeries heartRateSeries = new TimeSeries("Heart Rate");
        TimeSeries weightSeries = new TimeSeries("Weight");

        for (int i = 0; i < sampledDates.size(); i++) {
            Date date = sampledDates.get(i);
            if (date == null) {
                continue;
            }
            // 添加BMI数据
            BigDecimal bmi = sampledBMIs.get(i);
            if (bmi != null) {
                bmiSeries.add(new Day(date), bmi.doubleValue());
            }
            // 添加心率数据
            BigDecimal heartRate = sampledHeartRates.get(i);
            if (heartRate != null) {
                heartRateSeries.add(new Day(date), heartRate.doubleValue());
            }
            // 添加体重数据
            BigDecimal weight = sampledWeights.get(i);
            if (weight != null) {
                weightSeries.add(new Day(date), weight.doubleValue());
            }
        }

        // 创建数据集
        JFreeChart chart = getjFreeChart(bmiSeries, heartRateSeries, weightSeries);

        // 渲染图表为PNG图像
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(byteArrayOutputStream, chart, 800, 600);
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * 创建包含BMI、心率和体重三个指标的JFreeChart图表。
     * <p>
     * 该方法创建一个包含三个数据系列和对应Y轴的综合趋势图。
     *
     * @param bmiSeries       BMI数据的时间序列
     * @param heartRateSeries 心率数据的时间序列
     * @param weightSeries    体重数据的时间序列
     *
     * @return 包含三个指标的JFreeChart图表
     */
    private static JFreeChart getjFreeChart(TimeSeries bmiSeries, TimeSeries heartRateSeries, TimeSeries weightSeries) {
        TimeSeriesCollection bmiDataset = new TimeSeriesCollection(bmiSeries);
        TimeSeriesCollection heartRateDataset = new TimeSeriesCollection(heartRateSeries);
        TimeSeriesCollection weightDataset = new TimeSeriesCollection(weightSeries);

        // 创建轴
        DateAxis dateAxis = new DateAxis("Date");
        NumberAxis bmiAxis = new NumberAxis("BMI");
        NumberAxis heartRateAxis = new NumberAxis("Heart Rate");
        NumberAxis weightAxis = new NumberAxis("Weight");

        // 创建渲染器并设置样式
        XYLineAndShapeRenderer renderer0 = new XYLineAndShapeRenderer();
        renderer0.setSeriesPaint(0, Color.BLUE);

        XYLineAndShapeRenderer renderer1 = new XYLineAndShapeRenderer();
        renderer1.setSeriesPaint(0, Color.GREEN);

        XYLineAndShapeRenderer renderer2 = new XYLineAndShapeRenderer();
        renderer2.setSeriesPaint(0, Color.RED);

        // 创建XYPlot并设置数据集和轴
        XYPlot plot = new XYPlot(bmiDataset, dateAxis, bmiAxis, renderer0);
        plot.setDataset(1, heartRateDataset);
        plot.setRangeAxis(1, heartRateAxis);
        plot.setRenderer(1, renderer1);

        plot.setDataset(2, weightDataset);
        plot.setRangeAxis(2, weightAxis);
        plot.setRenderer(2, renderer2);

        // 创建图表
        return new JFreeChart("Health Trends Over Time", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
    }

    /**
     * 对数据进行采样，减少数据点数量。
     * <p>
     * 该方法通过均匀采样减少数据点数量，以优化图表生成性能。
     *
     * @param <T>     数据类型
     * @param data    原始数据列表
     * @param maxPoints 最大数据点数量
     *
     * @return 采样后的数据列表
     */
    // 数据采样函数
    public static <T> List<T> sampleData(List<T> data, int maxPoints) {
        if (data.size() <= maxPoints) {
            return data;
        }
        int step = data.size() / maxPoints;
        List<T> sampledData = new ArrayList<>();
        for (int i = 0; i < data.size(); i += step) {
            sampledData.add(data.get(i));
        }
        return sampledData;
    }

    /**
     * 生成用户健康数据的趋势图
     * @param healthDataTrendDTO 健康数据趋势的数据
     * @return 健康数据的趋势图
     * @throws IOException IOException
     */
    private static List<byte[]> generateTendencyChartOfHealthData(HealthDataTrendDTO healthDataTrendDTO) throws IOException {
        // 生成BMI趋势图
        byte[] bmiChart = ChartGenerateUtil.generateTrendChart(
                healthDataTrendDTO.getMeasurementDates(),
                healthDataTrendDTO.getBmis(),
                "BMI Trend Over Time",
                "BMI"
        );

        // 生成心率趋势图
        byte[] heartRateChart = ChartGenerateUtil.generateTrendChart(
                healthDataTrendDTO.getMeasurementDates(),
                healthDataTrendDTO.getHeartRates(),
                "Heart Rate Trend Over Time",
                "Heart Rate"
        );

        // 生成体重趋势图
        byte[] weightChart = ChartGenerateUtil.generateTrendChart(
                healthDataTrendDTO.getMeasurementDates(),
                healthDataTrendDTO.getWeights(),
                "Weight Trend Over Time",
                "Weight"
        );

        List<byte[]> byteArrayChartList = new ArrayList<>();
        byteArrayChartList.add(bmiChart);
        byteArrayChartList.add(heartRateChart);
        byteArrayChartList.add(weightChart);
        return byteArrayChartList;
    }

}
