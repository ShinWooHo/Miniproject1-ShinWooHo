package miniproject1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class MiniProject1 {
	// 필드
	private Scanner sc = new Scanner(System.in);
	private Connection conn;

	// 게시판 생성 메소드
	public void create() {
		System.out.println("만들예정");
		list();
	}

	// 게시판 상세 메소드
	public void read() {
		System.out.println("[게시물 읽기]");
		System.out.print("bno: ");
		int bno = Integer.parseInt(sc.nextLine());

		// boards 테이블에서 해당 게시물을 가져와 출력
		// boards 테이블에서 게시물 정볼를 가져와서 출력
		try {
			String sql = "" + "SELECT bno, btitle, bcontent, bwriter, bcreatedat, bupdatedat " + "FROM board "
					+ "WHERE bno=?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, bno);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				Board board = new Board();
				board.setBno(rs.getInt("bno"));
				board.setBtitle(rs.getString("btitle"));
				board.setBcontent(rs.getString("bcontent"));
				board.setBwriter(rs.getString("bwriter"));
				board.setBcreatedat(rs.getDate("bcreatedat"));
				board.setBupdatedat(rs.getDate("bupdatedat"));
				System.out.println("#################");
				System.out.println("번호: " + board.getBno());
				System.out.println("제목: " + board.getBtitle());
				System.out.println("내용: " + board.getBcontent());
				System.out.println("작성자: " + board.getBwriter());
				System.out.println("생성 날짜: " + board.getBcreatedat());
				System.out.println("수정 날짜: " + board.getBupdatedat());
				System.out.println("##################");
			}
			rs.close();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			exit();
		}
		list();
	}

	// 게시판 삭제 메소드
	public void clear() {
		System.out.println("삭제 만들 예정");
		list();
	}

	// 종료 메소드
	public void exit() {
		System.exit(0);
	}

	public MiniProject1() {
		try {
			// JDBC Dtiver 등록
			Class.forName("oracle.jdbc.OracleDriver");

			// 연결하기
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/xe", "java", "orcle");
		} catch (Exception e) {
			e.printStackTrace();
			exit();
		}
	}

	// 리스트 메소드
	public void list() {
		System.out.println();
		System.out.println("[게시물 목록]");
		System.out.println("------------------------------------------------------");
		System.out.printf("%-6s%-12s%-16s%-40s\n", "no", "writer", "date", "title");
		System.out.println("------------------------------------------------------");
		System.out.printf("%-6s%-12s%-16s%-40s \n", "1", "winter", "2022.01.27", "게시판에 오신 것을 환영합니다.");
		System.out.printf("%-6s%-12s%-16s%-40s \n", "2", "winter", "2022.01.27", "올 겨울은 많이 춥습니다.");
		mainMenu();
	}

	public void mainMenu() {
		System.out.println();
		System.out.println("---------------------------------------------");
		System.out.println("메인 메뉴: 1.Create | 2.Read | 3.Clear | 4.Exit");
		System.out.print("메인 선택: ");
		String menuNo = sc.nextLine();
		System.out.println();

		switch (menuNo) {
		case "1" -> create();
		case "2" -> read();
		case "3" -> clear();
		case "4" -> exit();
		}
	}

	public static void main(String[] args) {
		MiniProject1 m1 = new MiniProject1();
		m1.list();
	}
}
