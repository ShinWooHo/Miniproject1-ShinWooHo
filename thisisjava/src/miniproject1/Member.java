package miniproject1;

import java.util.Date;

import lombok.Data;

@Data
public class Member {
	private String mid; // 회원 아이디
	private String mname; // 회원 이름
	private String mpassword; // 회원 비밀번호
	private String mtel; // 회원 전화번호
	private String maddress; // 회원 주소
	private char mgender; // 회원 성별
	private Date mLastLoginTime; // 회원 마지막 로그인 시간
	private Date mLastLogoutTime; // 회원 마지막 로그아웃 시간
	private boolean menabled; // 회원 활성화 상태
	
}
