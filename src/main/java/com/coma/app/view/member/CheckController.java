package com.coma.app.view.member;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.coma.app.biz.member.MemberDTO;
import com.coma.app.biz.member.MemberService;
@Slf4j
@RestController	
public class CheckController {
	
	@Autowired
	private MemberService memberService;
	
	@PostMapping("/checkId.do")
	public @ResponseBody String checkId(@RequestBody MemberDTO memberDTO) {
		System.out.println("CheckController checkId 비동기 처리 로그");
		memberDTO.getMember_id();
		log.info("memberId : [{}]",memberDTO.getMember_id());
		//model 에 사용자 ID를 넘겨 값이 있는 지 확인 후
		memberDTO = this.memberService.selectOneSearchId(memberDTO);

		String result = "true";
		//값이 없으면 true 를 반환 합니다.
		if(memberDTO != null) {
			result	 = "false";
		}
		log.info("result : [{}]",result);
		// view 로 값을 전달 합니다.
		return result;
	}

	@PostMapping("/checkEmail.do")
	public @ResponseBody String checkEmail(@RequestBody MemberDTO memberDTO) {
		log.info("CheckController checkId 비동기 처리 로그");
		memberDTO.getMember_id();
		log.info("memberId : [{}]",memberDTO.getMember_id());
		//model 에 사용자 ID를 넘겨 값이 있는 지 확인 후
		memberDTO = this.memberService.selectOneSearchId(memberDTO);

		String result = "false";
		//값이 없으면 true 를 반환 합니다.
		if(memberDTO != null) {
			result	 = "true";
		}
		log.info("result : [{}]",result);
		// view 로 값을 전달 합니다.
		return result;
	}



	@PostMapping("/checkPassword.do")
	public @ResponseBody String checkPassword(MemberDTO memberDTO) {
		log.info("CheckController checkPassword 비동기 처리 로그");
		//model 에 사용자 ID를 넘겨 값이 있는 지 확인 후
		memberDTO = this.memberService.selectOneSearchIdPassword(memberDTO);
		
		String result = "false";
		//값이 없으면 true 를 반환 합니다.
		if(memberDTO != null) {
			result = "true";
		}
		// view 로 값을 전달 합니다.
		return result;
	}

}
