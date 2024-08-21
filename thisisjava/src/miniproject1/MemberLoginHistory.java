package miniproject1;

import java.util.Date;

import lombok.Data;

@Data
public class MemberLoginHistory {
	private int mseqId; // 시퀀스 기본키 
	private String mid; // 회원 아이디
	private Date mLastLoginTime; // 마지막 로그인 시간
	private Date mLastLogOutTime; // 마지막 로그아웃 시간
	
}
