package metaint.replanet.rest.pay.controller;

import lombok.extern.slf4j.Slf4j;
import metaint.replanet.rest.pay.dto.pay.DonationAmountDTO;
import metaint.replanet.rest.pay.dto.pay.KakaoPayApprovalVO;
import metaint.replanet.rest.pay.entity.Donation;
import metaint.replanet.rest.pay.entity.Member;
import metaint.replanet.rest.pay.entity.Pay;
import metaint.replanet.rest.pay.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.Setter;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class PayController {

    @Setter(onMethod_ = @Autowired)
    private PayService payService;


    @GetMapping("/kakaoPay")
    public void kakaoPayGet() {}

    @PostMapping("/kakaoPay")
    public String kakaoPay(@RequestBody DonationAmountDTO amount) {
        log.info("[POST /kakaoPay] -------------------------------------");
        log.info("[/kakaoPay cashAmount] : " + amount.getCashAmount());
        log.info("[/kakaoPay pointAmount] : " + amount.getPointAmount());
        log.info("[/kakaoPay finalAmount] : " + amount.getFinalAmount());
        // RequestBody에 담아온 기부액수를 들고와서 확인하는거

        String redirectUrl = payService.kakaoPayReady(amount);

        return "redirect:" + redirectUrl;
    }

    @PostMapping("/pointDonation")
    public ResponseEntity<Map<String, Integer>> pointDonation(@RequestBody DonationAmountDTO amount,
                                                              ModelAndView mv) {
        log.info("[POST /pointDonation] -------------------------------------");
        log.info("[/pointDonation cashAmount] : " + amount.getCashAmount()); // 0일거임
        log.info("[/pointDonation pointAmount] : " + amount.getPointAmount());
        log.info("[/pointDonation finalAmount] : " + amount.getFinalAmount());
        // RequestBody에 담아온 기부액수를 들고와서 확인하는거

        int payCode = payService.postPointDonation(amount);
        log.info("[GET /pointDonation] payCode : " + payCode);

        Map<String, Integer> response = new HashMap<>();
        response.put("payCode", payCode);
//        mv.setViewName("redirect:http://localhost:3000/donations/success?number=" + payCode);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/kakaoPaySuccess")
    public ModelAndView kakaoPaySuccess(@RequestParam("pg_token") String pg_token,
                                        @RequestParam("pointAmount") String pointAmount,
                                        ModelAndView mv) {
        log.info("[GET /kakaoPaySuccess]-------------------------------------");
        KakaoPayApprovalVO info = payService.kakaoPayInfo(pg_token, pointAmount);

        log.info("[GET /kakaoPaySuccess] info.getPayCode() : " + info.getPayCode());
        mv.setViewName("redirect:http://localhost:3000/donations/success?number=" + info.getPayCode());

        return mv;
    }

    @GetMapping("/kakaoPayCancle")
    public void kakaoPayCancel(HttpServletResponse response) {
        log.info("[GET /kakaoPayCancle]-------------------------------------");
        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader("Location", "http://localhost:3000/donations/cancel");
    }

    @GetMapping("/kakaoPaySuccessFail")
    public void kakaoPaySuccessFail(HttpServletResponse response) {
        log.info("[GET /kakaoPaySuccessFail]-------------------------------------");
        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader("Location", "http://localhost:3000/donations/fail");
    }

    @GetMapping("/pays")
    public ResponseEntity<List<Pay>> getPays(@RequestParam(required = false) String startDate,
                                             @RequestParam(required = false) String endDate) {

        log.info("[GET /pays] ----------------------------------------------");
        log.info("[GET /pays startDate] : " + startDate);
        log.info("[GET /pays endDate] : " + endDate);

        List<Pay> payList;

        if (startDate != null && endDate != null) {
            payList = payService.getPaysByDateRange(startDate, endDate);
        } else {
            payList = payService.getPays();
        }

        log.info("[/pays payList] : " + payList);
        log.info("[/pays payList.size()] : " + payList.size());

        return ResponseEntity.ok(payList);
    }

    @GetMapping("/donations/payCode={payCode}")
    public ResponseEntity<Pay> getDonationByTid(@PathVariable String payCode) {
        Pay pay = payService.getPayByPayCode(payCode);
        log.info("GET /donations/{payTid} pay : " + pay);

        if (pay != null) {
            Donation donation = pay.getRefDonation();
            if (donation != null) {
                return new ResponseEntity<>(pay, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping("/users/point/{memberCode}/donations")
    public ResponseEntity<Member> getPointByMember(@PathVariable String memberCode) {
        Member member = payService.getPointByMember(memberCode);
        log.info("GET /users/point/{memberCode}/donations member : " + member);

        if (member != null) {
            return new ResponseEntity<>(member, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
