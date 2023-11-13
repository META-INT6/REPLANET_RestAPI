package metaint.replanet.rest.chart.controller;


import io.swagger.annotations.ApiOperation;
import metaint.replanet.rest.chart.service.ChartService;
import metaint.replanet.rest.common.ResponseMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/charts")
public class ChartController {

    private final ChartService chartService;

    @Autowired
    public ChartController(ChartService chartService) {
        this.chartService = chartService;
    }


    @ApiOperation(value = "통계 조회 요청", notes = "현재 등록된 캠페인을 기준으로 통계를 조회합니다.", tags = {"차트 조회 컨트롤러"})
    @GetMapping("/series")
    public ResponseEntity<ResponseMessageDTO> selectChartResult() {
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(new MediaType("application","json", Charset.forName("UTF-8")));

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("카테고리별 통계 조회", chartService.countAndSumByCampaignCategory());
        responseMap.put("당해 통계 조회" , chartService.countAndSumByCurrentyear());
        responseMap.put("전해 통계 조회", chartService.countAndSumByPreviousyear());

        ResponseMessageDTO responseMessage = new ResponseMessageDTO(HttpStatus.OK, "조회성공!", responseMap);

        return new ResponseEntity<>(responseMessage, headers, HttpStatus.OK);
    }

}
