package com.coma.app.view.annotation;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
public class LoginCheckImpl {

    private static final String MEMBER_ID = "MEMBER_ID"; // 회원 ID를 나타내는 상수
    private static final String CREW_CHECK = "CREW_CHECK"; // 크루 체크를 나타내는 상수
    private LoginCheckImpl loginCheckImpl;

    //현재 요청과 응답, 세션 객체를 이용하여 로그인 정보를 검사하는 메서드.
    public void checkLogin(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String[] loginInfo = getLoginInformation(request, session); // 로그인 정보를 가져옴
        synchronizeLoginInformation(loginInfo, session); // 세션과 쿠키 간의 로그인 정보를 동기화

        if (loginInfo[0] == null) { // 로그인 정보가 없으면
            String redirectUrl = redirectLogin(); // 로그인 페이지로 리다이렉트 URL 설정
            if (redirectUrl != null) { // 리다이렉트 URL이 null이 아니면
                try {
                    response.sendRedirect(redirectUrl); // 로그인 페이지로 리다이렉트
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //로그아웃시 세션과 쿠키를 무효화하는 메서드
    public static void logout(HttpServletRequest request, HttpServletResponse response) {
        invalidateSession(request); // 세션 무효화
        clearCookies(request, response); // 쿠키 무효화
    }

    //세션을 무효화하는 메서드.
    private static void invalidateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // 세션이 존재하면 반환하고, 그렇지 않으면 null 반환
        if (session != null) { // 세션이 null이 아니면
            session.invalidate(); // 세션 무효화
        }
    }


    //쿠키를 무효화하는 메서드
    private static void clearCookies(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies(); // 요청에서 쿠키 배열을 가져옴
        if (cookies != null) { // 쿠키가 null이 아니면
            for (Cookie cookie : cookies) { // 모든 쿠키를 순회
                if (MEMBER_ID.equals(cookie.getName()) || CREW_CHECK.equals(cookie.getName())) { // 쿠키 이름이 MEMBER_ID 또는 CREW_CHECK인 경우
                    cookie.setMaxAge(0); // 쿠키를 무효화 (만료 시간 0 설정)
                    cookie.setPath("/"); // 전체 경로에 적용
                    response.addCookie(cookie); // 응답에 추가
                }
            }
        }
    }

    //요청과 세션 객체에서 로그인 정보를 가져오는 메서드

    private String[] getLoginInformation(HttpServletRequest request, HttpSession session) {
        //로그인 정보 배열 [MEMBER_ID, CREW_CHECK]
        String[] loginInfo = new String[2]; // 로그인 정보 배열 생성
        fillLoginInfoFromCookies(request, loginInfo); // 쿠키에서 로그인 정보를 가져와 배열에 저장
        fillLoginInfoFromSession(session, loginInfo); // 세션에서 로그인 정보를 가져와 배열에 저장
        return loginInfo; // 로그인 정보 배열 반환
    }

    private void fillLoginInfoFromCookies(HttpServletRequest request, String[] loginInfo) {
        Cookie[] cookies = request.getCookies(); // 요청에서 쿠키 배열을 가져옴
        if (cookies != null) { // 쿠키가 null이 아니면
            for (Cookie cookie : cookies) { // 모든 쿠키를 순회
                if (MEMBER_ID.equals(cookie.getName())) { // 쿠키 이름이 MEMBER_ID인 경우
                    loginInfo[0] = cookie.getValue(); // 배열의 첫 번째 요소에 쿠키 값을 저장
                } else if (CREW_CHECK.equals(cookie.getName())) { // 쿠키 이름이 CREW_CHECK인 경우
                    loginInfo[1] = cookie.getValue(); // 배열의 두 번째 요소에 쿠키 값을 저장
                }
            }
        }
    }

    private void fillLoginInfoFromSession(HttpSession session, String[] loginInfo) {
        if (loginInfo[0] == null) { // 배열의 첫 번째 요소가 null인 경우
            loginInfo[0] = (String) session.getAttribute(MEMBER_ID); // 세션에서 MEMBER_ID를 가져와 저장
        }
        if (loginInfo[1] == null) { // 배열의 두 번째 요소가 null인 경우
            loginInfo[1] = (String) session.getAttribute(CREW_CHECK); // 세션에서 CREW_CHECK를 가져와 저장
        }
    }

    private void synchronizeLoginInformation(String[] loginInfo, HttpSession session) {
        if (session.getAttribute(MEMBER_ID) == null && loginInfo[0] != null) { // 세션의 MEMBER_ID가 null이고 배열의 첫 번째 요소가 null이 아닌 경우
            session.setAttribute(MEMBER_ID, loginInfo[0]); // 세션의 MEMBER_ID에 배열의 첫 번째 요소를 저장
        }
        if (session.getAttribute(CREW_CHECK) == null && loginInfo[1] != null) { // 세션의 CREW_CHECK가 null이고 배열의 두 번째 요소가 null이 아닌 경우
            session.setAttribute(CREW_CHECK, loginInfo[1]); // 세션의 CREW_CHECK에 배열의 두 번째 요소를 저장
        }
    }

    private String redirectLogin() {
        return "redirect:login.do"; // 로그인 페이지 URL 반환
    }

    // 생성자 또는 세터 메서드를 통해 DI 설정
    public void setLoginCheckImpl(LoginCheckImpl loginCheckImpl) {
        this.loginCheckImpl = loginCheckImpl;
    }
}