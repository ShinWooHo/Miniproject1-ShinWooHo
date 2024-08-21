package miniproject1;

import java.util.Date;

import lombok.Data;


@Data
public class Board {
	private int bno; // 게시판 번호
	private String btitle; // 게시판 제목
	private String bwriter; // 게시판 글쓴이
	private String bcontent; // 게시판 내용
	private Date bcreatedat; // 게시판 작성일자
	private Date bupdatedat; // 게시판 수정일자
	private int bhitcount; // 게시판 조회수
	private boolean benabled; // 게시판 삭제 시 0으로 설정 위함 필드
}


