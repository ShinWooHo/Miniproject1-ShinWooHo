package miniproject1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.regex.Pattern;

import com.mysql.cj.jdbc.CallableStatement;

public class miniproject1_1 {
	private static final Scanner sc = new Scanner(System.in);
	private static Connection conn;
	// 로그인 상태 저장
	private static String currentUser = null;

	// 데이터베이스 연결 설정
	public static void setupDatabase() {
		try {
			Class.forName("oracle.jdbc.OracleDriver");
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/xe", "user01", "1004");
			if (conn != null && !conn.isClosed()) {
				System.out.println("DB 연결 성공");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void mainMenu() {
		while (true) {
			System.out.println("-------------------");
			System.out.println();
			System.out.println("1. 회원가입");
			System.out.println("2. 로그인");
			System.out.println("3. 아이디 찾기");
			System.out.println("4. 비밀번호 초기화");
			System.out.println("5. 종료");
			System.out.println();
			System.out.print("원하는 기능? ");
			String menuNo = sc.nextLine();

			switch (menuNo) {
			case "1" -> signUp();
			case "2" -> signIn();
			case "3" -> {
				System.out.print("이름: ");
				String mname = sc.nextLine();
				System.out.print("비번: ");
				String mpassword = sc.nextLine();
				findId(mname, mpassword);
			}
			case "4" -> {
				System.out.print("아이디: ");
				String mid = sc.nextLine();
				System.out.print("전화번호: ");
				String mtel = sc.nextLine();
				resetPw(mid, mtel);
			}
			case "5" -> exit();
			default -> System.out.println("잘못된 입력입니다. 다시 시도해 주세요.");
			}
		}
	}

	// 회원가입 기능
	public void signUp() {
		System.out.println("회원 가입화면");
		// 아이디 정규식 패턴
		String regMid = "^[a-zA-Z][a-zA-Z0-9._]{4,14}$";
		String mid;
		while (true) {
			System.out.print("아이디: ");
			mid = sc.nextLine();
			boolean result = Pattern.matches(regMid, mid);
			if (result) {
				break; // 정규식에 맞으면 반복을 정료
			} else {
				System.out.println("최소 5자에서 최대 15자 이내로 입력해 주세요.");
			}
		}
		// 비밀번호 정규식 패턴
		String regMpassword = "^(?=.*\\d)[A-Za-z\\d@$!%*?&]{8,}$";
		String mpassword;
		while (true) {
			System.out.print("비번: ");
			mpassword = sc.nextLine();
			boolean result = Pattern.matches(regMpassword, mpassword);
			if (result) {
				break;
			} else {
				System.out.println("비밀번호를 8자 이상 입력해주세요.");
			}
		}

		// 이름 정규식 패턴
		String regMname = "^[가-힣a-zA-Z]{2,20}$";
		String mname;
		while (true) {
			System.out.print("이름: ");
			mname = sc.nextLine();
			boolean result = Pattern.matches(regMname, mname);
			if (result) {
				break;
			} else {
				System.out.println("이름은 2자이상 20자 이내로 입력해 주세요.");
			}
		}

		// 휴대전화번호 정규식 패턴
		String regMtel = "^(\\d{2,3})-(\\d{3,4})-(\\d{4})$|^\\d{9,11}$";
		String mtel;
		while (true) {
			System.out.print("전화번호: ");
			mtel = sc.nextLine();
			boolean result = Pattern.matches(regMtel, mtel);
			if (result) {
				break;
			} else {
				System.out.println("전화번호 형식이 올바르지 않습니다.");
			}
		}

		// 주소도 정규식 패턴 해야함
		System.out.print("주소: ");
		String maddress = sc.nextLine();

		// 성별 정규식 패턴
		String regMgender = "^[MF]$";
		String mgender;
		while (true) {
			System.out.print("성별: ");
			mgender = sc.nextLine().toUpperCase(); // 대소문자 구분없이 받기 위함
			boolean result = Pattern.matches(regMgender, mgender);
			if (result) {
				break;
			} else {
				System.out.println("형식이 올바르지 않습니다.");
			}
		}
		System.out.println();
		System.out.println("1. 가입");
		System.out.println("2. 다시입력");
		System.out.println("3. 이전 화면으로");
		System.out.println();
		System.out.print("원하는 기능: ");
		String selectNo = sc.nextLine();

		switch (selectNo) {
		case "1" -> saveMember(mid, mpassword, mname, mtel, maddress, mgender);
		case "2" -> signUp();
		case "3" -> mainMenu();
		default -> System.out.println("잘못된 입력입니다. 다시 시도해 주세요.");
		}
	}

	// 회원가입 정보 임시 저장 기능
	private void saveMember(String mid, String mpassword, String mname, String mtel, String maddress, String mgender) {
		try {
			String sql = ""
					+ "INSERT INTO member (mid, mname, mpassword, mtel, maddress, mgender, mlastlogintime, mlastlogouttime, menabled) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, mid);
			pstmt.setString(2, mname);
			pstmt.setString(3, mpassword);
			pstmt.setString(4, mtel);
			pstmt.setString(5, maddress);
			pstmt.setString(6, mgender);
			pstmt.setTimestamp(7, null);
			pstmt.setTimestamp(8, null);
			pstmt.setInt(9, 1);

			int rowsAffected = pstmt.executeUpdate();
			if (rowsAffected > 0) {
				System.out.println("회원가입에 성공하셨습니다.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 로그인 기능
	public void signIn() {
		while (true) { // 무한 루프, 잘못된 입력 시 로그인 화면을 계속 표시
			System.out.println("로그인 화면");
			System.out.print("아이디: ");
			String mid = sc.nextLine();
			System.out.print("비번: ");
			String mpassword = sc.nextLine();
			System.out.println();
			System.out.println("1. 로그인");
			System.out.println("2. 다시입력");
			System.out.println("3. 이전 화면으로");

			String selectNum = sc.nextLine();
			switch (selectNum) {
			case "1" -> {
				saveLogin(mid, mpassword);
				System.out.println();
				return; // 로그인 성공 시 메서드 종료
			}
			case "2" -> {
				// 사용자가 '다시입력'을 선택했으므로 아무 작업도 하지 않고 루프를 계속
			}
			case "3" -> {
				mainMenu();
				return; // 이전 화면으로 이동 후 메서드 종료
			}
			default -> System.out.println("잘못된 입력입니다. 다시 시도해 주세요.");
			}
		}
	}

	// 로그인 데이터 저장 기능
	private void saveLogin(String mid, String mpassword) {
		try {
			// 자동 커밋 비활성화
			conn.setAutoCommit(false);

			// 회원 정보를 조회하는 쿼리
			String sql = "SELECT * FROM MEMBER WHERE MID = ? AND MPASSWORD = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, mid);
			pstmt.setString(2, mpassword);

			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				if (rs.getInt("menabled") == 0) {
					System.out.println("비활성화된 계정입니다.");
					conn.rollback(); // 롤백
					return;
				}
				System.out.println("로그인 성공했습니다.!");
				System.out.println();

				// 로그인된 유저의 아이디를 저장
				currentUser = mid;
				String mrole = rs.getString("mrole"); // 권한을 가져옴

				// mlastlogintime을 현재 시간(SYSDATE)으로 업데이트
				String updateSql = "UPDATE MEMBER SET mlastlogintime = SYSDATE WHERE MID = ?";
				PreparedStatement updatePstmt = conn.prepareStatement(updateSql);
				updatePstmt.setString(1, mid);

				int rowsUpdated = updatePstmt.executeUpdate();
				// 로그인 시간 업데이트
				System.out.println("업데이트된 행 수: " + rowsUpdated);

				conn.commit(); // 변경사항 커밋
				updatePstmt.close(); // 자원 해제
				userMenu(mrole);
			} else {
				System.out.println("아이디 또는 비밀번호가 일치하지 않습니다.");
			}
			rs.close(); // ResultSet 자원 해제
			pstmt.close(); // PreparedStatement 자원 해제
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (conn != null) {
					conn.rollback(); // 예외 발생 시 롤백
				}
			} catch (SQLException se) {
				se.printStackTrace();
			}
		} finally {
			try {
				if (conn != null) {
					conn.setAutoCommit(true); // 자동 커밋 모드 다시 활성화
				}
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
	}

	// 사용자 메뉴
	private void userMenu(String mrole) {
		String selectNum = "";
		int currentPage = 1;
		int pageSize = 10;
		while (currentUser != null) {
			// 메뉴 출력
			if ("ROLE_ADMIN".equals(mrole)) {
				System.out.println("0. 정보 수정");
				System.out.println("1. 나의 정보확인");
				System.out.println("2. 게시물 목록");
				System.out.println("3. 회원 목록 조회");
				System.out.println("4. 로그아웃");
				System.out.println("5. 종료");
				System.out.println("6. 탈퇴");
				System.out.println("7. 비밀번호 변경");
				System.out.print("원하는 기능 ? ");
				selectNum = sc.nextLine();
			} else {
				System.out.println("0. 정보 수정");
				System.out.println("1. 나의 정보확인");
				System.out.println("2. 게시물 목록");
				System.out.println("3. 로그아웃");
				System.out.println("4. 종료");
				System.out.println("5. 탙퇴");
				System.out.println("6. 비밀번호 변경");
				System.out.print("원하는 기능 ? ");
				selectNum = sc.nextLine();
			}

			// 입력 처리
			switch (selectNum) {
			case "0" -> {
				System.out.println("비밀번호를 입력해 주세요.");
				System.out.print("현재 비밀번호: ");
				String oldMpassword = sc.nextLine();

				System.out.print("새 비밀번호: ");
				String newMpassword = sc.nextLine(); // 새로운 비밀번호
				System.out.print("새 이름: ");
				String mname = sc.nextLine();
				System.out.print("새 번호: ");
				String mtel = sc.nextLine();
				System.out.print("새 주소: ");
				String maddress = sc.nextLine();
				System.out.print("새 권한: ");
				mrole = sc.nextLine();

				updateMypage(oldMpassword, newMpassword, mname, mtel, maddress, mrole);
				System.out.println();
			}

			case "1" -> {
				selectMypage();
			}
			case "2" -> {
				pagiNation();
			}
			case "3" -> {
				if ("ROLE_ADMIN".equals(mrole)) {
					viewAllMember();
					System.out.println();
				} else {
					logout(currentUser);
				}
			}
			case "4" -> {
				if ("ROLE_ADMIN".equals(mrole)) {
					logout(currentUser);
				} else {
					exit();
				}
			}
			case "5" -> {
				if ("ROLE_ADMIN".equals(mrole)) {
					// 관리자 권한으로 비밀번호 없이 비활성화
					exit(); // 현재 로그인한 사용자를 비활성화
				} else {
					// 사용자 권한으로 자신의 비밀번호 확인 후 비활성화
					System.out.print("비밀번호를 입력해 주세요.");
					String mpassword = sc.nextLine();
					DeleteMember(currentUser, mpassword);
				}
			}
			case "6" -> {
				if ("ROLE_ADMIN".equals(mrole)) {
					// 관리자 권한으로 비밀번호 없이 비활성화
					DeleteMember(currentUser, null); // 현재 로그인한 사용자를 비활성화
				} else {
					// 사용자 권한으로 자신의 비밀번호 확인 후 비활성화
					System.out.println("잘못 입력하셨스빈다.");
				}
			}
			case "7" -> {
				if ("ROLE_ADMIN".equals(mrole)) {
					System.out.print("아이디를 입력해 주세요.");
					String mid = sc.nextLine();
					System.out.print("비밀번호를 입력해 주세요.");
					String mpassword = sc.nextLine();
					loginRePassword(mid, mpassword);
				} else {
					System.out.println("잘못된 입력입니다. 다시 시도해 주세요.");
				}

			}
			default -> System.out.println("잘못된 입력입니다. 다시 시도해 주세요.");
			}
		}
	}

	// 0.정보 수정 기능
	public static void updateMypage(String oldpassword, String newpassword, String mname, String mtel, String maddress,
			String mrole) {
		try {
			// JDBC 드라이버 등록
			Class.forName("oracle.jdbc.OracleDriver");

			// 데이터베이스 연결
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/xe", "user01", "1004");

			// 현재 비밀번호 확인 쿼리
			String selectSql = "SELECT mpassword FROM member WHERE mid = ?";
			PreparedStatement selectPstmt = conn.prepareStatement(selectSql);
			selectPstmt.setString(1, currentUser);
			ResultSet rs = selectPstmt.executeQuery();

			if (rs.next()) {
				// 비밀번호 확인
				if (!rs.getString("mpassword").equals(oldpassword)) {
					System.out.println("현재 비밀번호가 일치하지 않습니다.");
					return;
				} else {
					System.out.println("비밀번호가 확인되었습니다.");
				}
			} else {
				System.out.println("사용자를 찾을 수 없습니다.");
				return;
			}
			// 정보 수정 쿼리
			String updateSql = "UPDATE member SET mpassword = ?, mname = ?, mtel = ?, maddress = ?, mrole = ? WHERE mid = ?";
			PreparedStatement updatePstmt = conn.prepareStatement(updateSql);
			updatePstmt.setString(1, newpassword); // 새로운 비밀번호
			updatePstmt.setString(2, mname); // 새 이름
			updatePstmt.setString(3, mtel); // 새 전화번호
			updatePstmt.setString(4, maddress); // 새 주소
			updatePstmt.setString(5, mrole); // 새 권한
			updatePstmt.setString(6, currentUser); // 사용자 ID

			int rowsUpdated = updatePstmt.executeUpdate();
			if (rowsUpdated > 0) {
				System.out.println("회원 정보를 수정했습니다.");
			} else {
				System.out.println("회원 정보 수정에 실패했습니다.");
			}
			updatePstmt.close();

			rs.close();
			selectPstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 1.나의 정보 확인 기능
	public static void selectMypage() {
		try {
			String selectSql = "SELECT * FROM member WHERE mid = ?";
			PreparedStatement selectPstmt = conn.prepareStatement(selectSql);
			selectPstmt.setString(1, currentUser);
			ResultSet rs = selectPstmt.executeQuery();

			if (rs.next()) {
				System.out.println("정보들을 조회합니다.");
				System.out.printf("아이디: %s\n이름: %s\n비밀번호: %s\n전화번호: %s\n주소: %s\n성별: %s\n", rs.getString("mid"),
						rs.getString("mname"), rs.getString("mpassword"), rs.getString("mtel"),
						rs.getString("maddress"), rs.getString("mgender"));
				System.out.printf("최근 로그인: %s\n최근 로그아웃: %s\n", rs.getTimestamp("mlastlogintime"),
						rs.getTimestamp("mlastlogouttime"));
				System.out.println();
			} else {
				System.out.println("정보 조회에 실패했습니다. 해당 아이디를 찾을 수 없습니다.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 2.게시물 목록 pageNumber - 페이지 번호, pageSize - 게시물 수
	public static boolean BoardRead(int pageNumber, int pageSize) {
		System.out.println("[게시물 목록]");

		try {
			int startRow = (pageNumber - 1) * pageSize + 1;
			int endRow = pageNumber * pageSize;

			String sql = "SELECT * FROM ("
					+ "    SELECT rownum AS rn, bno, btitle, bwriter, bcontent, bcreatedat, bupdatedat, bhitcount, benabled "
					+ "    FROM ("
					+ "        SELECT bno, btitle, bwriter, bcontent, bcreatedat, bupdatedat, bhitcount, benabled "
					+ "        FROM board " + "        WHERE benabled = 1 " + "        ORDER BY bcreatedat ASC "
					+ "    ) " + "    WHERE rownum <= ? " + ") " + "WHERE rn >= ?";

			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, endRow);
			pstmt.setInt(2, startRow);

			ResultSet rs = pstmt.executeQuery();

			// 결과가 없으면 페이지에 데이터가 없는 경우 처리
			if (!rs.isBeforeFirst()) {
				rs.close();
				pstmt.close();
				return false;
			}

			// 결과가 있을 경우 게시물 목록 출력
			System.out.println("-------------------------------------------------------------------------------");
			System.out.printf("%-4s %-15s %-30s %-10s %-20s\n", "no", "writer", "title", "hitcount", "createdat");
			System.out.println("-------------------------------------------------------------------------------");

			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

			LocalDateTime now = LocalDateTime.now();

			while (rs.next()) {
				LocalDateTime createdAt = null;
				if (rs.getTimestamp("bcreatedat") != null) {
					createdAt = rs.getTimestamp("bcreatedat").toLocalDateTime();
				}
				// 게시물 작성 시간을 문자열로 설정위함
				String createdTimeStr = "null";

				if (createdAt != null) {
					Duration duration = Duration.between(createdAt, now);

					if (duration.toHours() < 24) {
						createdTimeStr = createdAt.format(timeFormatter);
					} else {
						createdTimeStr = createdAt.format(dateFormatter);
					}
				}
				System.out.printf("%-4d %-15s %-30s %-10d %-20s\n", rs.getInt("bno"), rs.getString("bwriter"),
						rs.getString("btitle"), rs.getInt("bhitcount"), createdTimeStr);
			}
			System.out.println(
					"----------------------------------------------------------------------------------------------------------------------------------");

			rs.close();
			pstmt.close();
			return true; // 데이터가 존재함
		} catch (Exception e) {
			e.printStackTrace();
			return false; // 오류 발생 시 데이터가 없다고 간주
		}
	}

	// 3.로그아웃 기능
	public static void logout(String mid) {
		try {
			// 현재 시간 가져오기
			Timestamp now = new Timestamp(System.currentTimeMillis());
			// 사용자 로그아웃 시간 업데이트 처리
			String sql = "UPDATE MEMBER SET mlastlogouttime = SYSDATE WHERE mid = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, mid);

			// 쿼리문 실행
			int rowsAffected = pstmt.executeUpdate();
			if (rowsAffected > 0) {
				System.out.println("로그아웃에 성공하였습니다.");
				System.out.println();
				// 로그인한 유저를 로그아웃 함
				currentUser = null;
			} else {
				System.out.println("로그아웃에 실패했습니다.");
				System.out.println();
			}
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 4.프로그램 종료
	public static void exit() {
		System.out.println("프로그램을 종료합니다.");
		System.exit(0);
	}

	// 5.탈퇴 기능
	public static void DeleteMember(String mid, String mpassword) {
		// 쿼리 정의
		String selectPasswordSql = "SELECT mpassword FROM member WHERE mid = ?";
		String updateMemberSql = "UPDATE member SET menabled = 0 WHERE mid = ?";
		String getRoleSql = "SELECT mrole FROM member WHERE mid = ?";

		try {
			// 사용자 권한 조회
			String userRole = "";
			try (PreparedStatement getRolePstmt = conn.prepareStatement(getRoleSql)) {
				getRolePstmt.setString(1, mid);
				try (ResultSet getRoleRs = getRolePstmt.executeQuery()) {
					if (getRoleRs.next()) {
						userRole = getRoleRs.getString("mrole");
					}
				}
			}

			// 비밀번호 확인 및 비활성화
			if ("ROLE_ADMIN".equals(userRole)) {
				// 관리자 권한으로 비밀번호 없이 비활성화
				try (PreparedStatement updatePstmt = conn.prepareStatement(updateMemberSql)) {
					updatePstmt.setString(1, mid);
					int rowsUpdated = updatePstmt.executeUpdate();

					if (rowsUpdated > 0) {
						System.out.println("회원이 비활성화 되었습니다.");
						conn.commit(); // 변경사항 커밋
					} else {
						System.out.println("회원 비활성화에 실패했습니다.");
					}
				}
			} else {
				// 일반 사용자로 비밀번호 확인 후 비활성화
				try (PreparedStatement selectPstmt = conn.prepareStatement(selectPasswordSql)) {
					selectPstmt.setString(1, mid);
					try (ResultSet rs = selectPstmt.executeQuery()) {
						if (rs.next()) {
							String storedPassword = rs.getString("mpassword");

							// 비밀번호가 일치하는 경우 회원 비활성화
							if (storedPassword.equals(mpassword)) {
								try (PreparedStatement updatePstmt = conn.prepareStatement(updateMemberSql)) {
									updatePstmt.setString(1, mid);
									int rowsUpdated = updatePstmt.executeUpdate();

									if (rowsUpdated > 0) {
										System.out.println("회원이 비활성화 되었습니다.");
										conn.commit(); // 변경사항 커밋
									} else {
										System.out.println("회원 비활성화에 실패했습니다.");
									}
								}
							} else {
								System.out.println("비밀번호가 일치하지 않습니다.");
							}
						} else {
							System.out.println("회원을 찾을 수 없습니다.");
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println("SQL 오류 발생: " + e.getMessage());
			e.printStackTrace();
			try {
				if (conn != null) {
					conn.rollback(); // 롤백 추가
				}
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
	}

	// 페이지 이동 기능
	public static void pagiNation() {
		int currentPage = 1;
		int pageSize = 10;

		while (true) {
			boolean hasData = BoardRead(currentPage, pageSize);

			if (!hasData && currentPage != 1) {
				// 데이터가 없는 페이지로 이동하려는 경우
				System.out.println("해당 페이지에는 게시물이 존재하지 않습니다. 이전 페이지로 돌아갑니다.");
				currentPage--;
				continue; // 현재 페이지에 머무름

			}

			System.out.println("현재 페이지: " + currentPage);
			System.out.println("다음 페이지 (n), 이전 페이지 (p), 페이지 번호 입력 (숫자), 종료 (q)");
			String selectNum = sc.nextLine();

			if (selectNum.equalsIgnoreCase("n")) {
				currentPage++;
			} else if (selectNum.equalsIgnoreCase("p")) {
				if (currentPage > 1) {
					currentPage--;
				} else {
					System.out.println("첫 페이지입니다.");
				}
			} else if (selectNum.matches("\\d+")) {
				int page = Integer.parseInt(selectNum);
				if (page > 0) {
					currentPage = page;
				} else {
					System.out.println("올바른 페이지 번호를 입력해 주세요.");
				}
			} else if (selectNum.equalsIgnoreCase("q")) {
				break;
			} else {
				System.out.println("잘못된 입력입니다. 다시 시도해 주세요.");
			}
		}

		while (true) {
			System.out.println();
			System.out.println("1. Create");
			System.out.println("2. Read");
			System.out.println("3. Clear");
			System.out.println("4. Update");
			System.out.println("5. Exit");
			String selectNum = sc.nextLine();

			switch (selectNum) {
			case "1" -> {
				System.out.print("비밀번호를 입력하세요: ");
				String inputMpassword = sc.nextLine();
				System.out.print("제목: ");
				String btitle = sc.nextLine();
				System.out.print("작성자: ");
				String bwriter = sc.nextLine();
				System.out.print("내용: ");
				String bcontent = sc.nextLine();
				BoardInsert(currentUser, inputMpassword, btitle, bwriter, bcontent);
			}
			case "2" -> {
				System.out.print("상세보기 원하는 bno를 입력해주세요.");
				int bno = Integer.parseInt(sc.nextLine());
				BoardDetail(bno);
			}
			case "3" -> BoardDelete();
			case "4" -> BoardUpdate();
			case "5" -> exit();
			default -> System.out.println("잘못된 입력입니다. 다시 입력해 주세요.");
			}
		}

	}

	// 권한이 관리자인 경우 회원 목록 조회 기능
	public static void viewAllMember() {
		try {
			String sql = "select * from member";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				System.out.println("[회원 목록]");
				System.out.println("-------------------------------------------------------------------");
				System.out.printf("%-10s %-10s %-15s %-20s %-6s\n", "아이디", "이름", "전화번호", "주소", "성별");
				System.out.println("-------------------------------------------------------------------");

				while (rs.next()) {
					System.out.printf("%-10s %-10s %-15s %-20s %-6s\n", rs.getString("mid"), rs.getString("mname"),
							rs.getString("mtel"), rs.getString("maddress"), rs.getString("mgender"));
				}
				rs.close();
				pstmt.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 아이디 찾기 기능
	public void findId(String mname, String massword) {
		try {
			String sql = "SELECT MID FROM member WHERE mname =? AND mpassword =?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, mname);
			pstmt.setString(2, massword);

			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				String foundId = rs.getString("MID");
				System.out.println("아이디를 찾았습니다." + " " + "[" + foundId + "]");
			} else {
				System.out.println("아이디 찾기를 실패했습니다.");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 6.비밀번호 초기화 기능(비밀번호를 까먹은 경우)
	public void resetPw(String mid, String mtel) {
		try {
			String selectSql = "SELECT mid FROM member WHERE mid = ? AND mtel = ?";
			PreparedStatement selectPstmt = conn.prepareStatement(selectSql);
			selectPstmt.setString(1, mid);
			selectPstmt.setString(2, mtel);

			ResultSet selectRs = selectPstmt.executeQuery();
			if (selectRs.next()) {
				System.out.print("새 비밀번호를 입력하세요: ");
				String resetMpassword = sc.nextLine();

				// 비밀번호 업데이트하는 SQL 쿼리
				String updateSql = "UPDATE member SET mpassword = ? WHERE mid = ?";
				PreparedStatement updatePstmt = conn.prepareStatement(updateSql);
				updatePstmt.setString(1, resetMpassword);
				updatePstmt.setString(2, mid);
				int updateRows = updatePstmt.executeUpdate();

				// 비밀번호 변경 결과에 따른 메시지 출력
				if (updateRows > 0) {
					System.out.println("비밀번호가 성공적으로 변경되었습니다.");
				} else {
					System.out.println("비밀번호 변경에 실패했습니다.");
				}

			} else {
				// 아이디 또는 비밀번호가 맞지 않는 경우
				System.out.println("아이디 또는 비밀번호가 일치하지 않습니다.");
			}
			selectRs.close();
			selectPstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 7.비밀번호 변경 (변경할 경우)
	public static void loginRePassword(String mid, String mpassword) {
		try {
			// JDBC 드라이버 등록
			Class.forName("oracle.jdbc.OracleDriver");

			// 데이터베이스 연결
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/xe", "user01", "1004");

			String selectSql = "SELECT mid FROM member WHERE mid = ? AND mpassword = ?";
			PreparedStatement selectPstmt = conn.prepareStatement(selectSql);
			selectPstmt.setString(1, mid);
			selectPstmt.setString(2, mpassword);

			ResultSet rs = selectPstmt.executeQuery();
			if (rs.next()) {
				if (currentUser.equals(mid)) {
					System.out.print("새 비밀번호를 입력해 주세요.");
					String resetMpassword = sc.nextLine();

					// 비밀번호를 업데이트하는 쿼리
					String updateSql = "UPDATE member SET mpassword = ? WHERE mid = ?";
					PreparedStatement updatePstmt = conn.prepareStatement(updateSql);
					updatePstmt.setString(1, resetMpassword);
					updatePstmt.setString(2, mid);

					int rowsUpdated = updatePstmt.executeUpdate();
					if (rowsUpdated > 0) {
						System.out.println("비밀번호를 변경하셨습니다.");
					} else {
						System.out.println("비밀번호 변경을 실패하셨습니다.");
					}
				} else {
					System.out.println("본인 계정만 비밀번호를 변경할 수 있습니다.");
				}

			} else {
				System.out.println("아이디 또는 비밀번호가 일치하지 않습니다.");
			}

			rs.close();
			selectPstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 다시 입력받기
	public void reInput() {
		signUp();
	}

	// 게시물 생성
	public static void BoardInsert(String currentUser, String inputMpassword, String btitle, String bwriter,
			String bcontent) {
		try {
			if (conn == null || conn.isClosed()) {
				System.out.println("DB 연결이 유효하지 않습니다.");
				setupDatabase();
			}

			// 사용자의 비밀번호 확인
			String getMemberSql = "SELECT mpassword FROM member WHERE mid = ?";
			PreparedStatement getPstmt = conn.prepareStatement(getMemberSql);
			getPstmt.setString(1, currentUser);
			ResultSet selectRs = getPstmt.executeQuery();

			String storedPassword = "";
			if (selectRs.next()) {
				storedPassword = selectRs.getString("mpassword");
			}

			if (!storedPassword.equals(inputMpassword)) {
				System.out.println("비밀번호가 일치하지 않습니다.");
				return; // 비밀번호가 일치하지 않으면 종료
			}

			// 게시물 등록
			String insertSql = "INSERT INTO board (bno,mid, btitle, bwriter, bcontent, bcreatedat, bupdatedat, bhitcount, benabled) "
					+ "VALUES (BNO_SEQ.NEXTVAL,?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement insertPstmt = conn.prepareStatement(insertSql);
			insertPstmt.setString(1, currentUser); // mid 외래키로 저장
			insertPstmt.setString(2, btitle);
			insertPstmt.setString(3, bwriter);
			insertPstmt.setString(4, bcontent);

			// 현재 시간 설정
			LocalDateTime now = LocalDateTime.now();
			insertPstmt.setTimestamp(5, Timestamp.valueOf(now)); // bcreatedat
			insertPstmt.setTimestamp(6, Timestamp.valueOf(now)); // bupdatedat
			insertPstmt.setInt(7, 0); // bhitcount
			insertPstmt.setBoolean(8, true); // benabled

			int result = insertPstmt.executeUpdate();

			if (result > 0) {
				System.out.println("게시물이 성공적으로 등록되었습니다.");
			} else {
				System.out.println("게시물 등록에 실패했습니다.");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null && !conn.isClosed())
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// 게시물 삽입 로직 메솓

	// 게시물 상세보기 기능
	public static void BoardDetail(int bno) {
		try {
			// 게시물 조회수 증가
			String updateSql = "UPDATE board SET bhitcount = bhitcount + 1 WHERE bno = ? AND benabled = 1";
			PreparedStatement updatePstmt = conn.prepareStatement(updateSql);
			updatePstmt.setInt(1, bno);
			int rowsAffected = updatePstmt.executeUpdate();
			updatePstmt.close();

			// 게시물 상세 조회
			String sql = "SELECT bno, mid, btitle, bwriter, bcontent, bcreatedat, bupdatedat, bhitcount FROM board WHERE bno = ? AND benabled = 1";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, bno);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				System.out.println(
						"----------------------------------------------------------------------------------------------------------------------------------");
				System.out.printf("%-4s %-15s %-30s %-10s %-20s %-20s %-30s\n", "No", "Writer", "Title", "HitCount",
						"CreatedAt", "UpdatedAt", "Content");
				System.out.println(
						"----------------------------------------------------------------------------------------------------------------------------------");

				DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

				LocalDateTime now = LocalDateTime.now();

				LocalDateTime createdAt = rs.getTimestamp("bcreatedat") != null
						? rs.getTimestamp("bcreatedat").toLocalDateTime()
						: null;
				String createdTimeStr = createdAt != null && Duration.between(createdAt, now).toHours() < 24
						? createdAt.format(timeFormatter)
						: (createdAt != null ? createdAt.format(dateFormatter) : "null");

				LocalDateTime updatedAt = rs.getTimestamp("bupdatedat") != null
						? rs.getTimestamp("bupdatedat").toLocalDateTime()
						: null;
				String updatedTimeStr = updatedAt != null && Duration.between(updatedAt, now).toHours() < 24
						? updatedAt.format(timeFormatter)
						: (updatedAt != null ? updatedAt.format(dateFormatter) : "null");

				System.out.printf("%-4d %-15s %-30s %-10d %-20s %-20s %-30s\n", rs.getInt("bno"),
						rs.getString("bwriter"), rs.getString("btitle"), rs.getInt("bhitcount"), createdTimeStr,
						updatedTimeStr, rs.getString("bcontent"));
				System.out.println();

				// 회원에서 권한을 가져오기 위함
				String selectSql = "SELECT mrole FROM member WHERE mid = ?";
				PreparedStatement selectPstmt = conn.prepareStatement(selectSql);
				selectPstmt.setString(1, currentUser);
				ResultSet selectRs = selectPstmt.executeQuery();
				String userRole = "";
				if (selectRs.next()) {
					userRole = selectRs.getString("mrole");
				}

				// 본인이 작성한 게시물일 경우 수정, 삭제
				if (rs.getString("mid").equals(currentUser) || "ROLE_ADMIN".equals(userRole)) {
					System.out.println("1. 게시물 수정하기");
					System.out.println("2. 게시물 삭제하기");
					String selectNum = sc.nextLine();

					switch (selectNum) {
					case "1" -> {
						BoardUpdate(bno);
						conn.commit();
					}
					case "2" -> BoardDelete();
					}
				}
			} else {
				System.out.println("해당 번호의 게시물이 존재하지 않습니다.");
			}

			rs.close();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 데이터 베이스연결 & 수정할 bno입력받아 게시물 id,pw를 가져오기
	public static void BoardUpdate() {
		try {
			// JDBC 드라이버 등록
			Class.forName("oracle.jdbc.OracleDriver");

			// 데이터베이스 연결
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/xe", "user01", "1004");

			System.out.print("수정할 게시물 번호를 입력해 주세요: ");
			int bno = Integer.parseInt(sc.nextLine());

			// 작성자의 정보 가져오기
			String getWriterSql = "SELECT mid FROM board WHERE bno = ? AND benabled = 1";
			PreparedStatement getWriterPstmt = conn.prepareStatement(getWriterSql);
			getWriterPstmt.setInt(1, bno);
			ResultSet rs = getWriterPstmt.executeQuery();

			// 현재 사용자의 권한 및 비밀번호 가져오기
			String selectSql = "SELECT mrole, mpassword FROM member WHERE mid = ?";
			PreparedStatement selectPstmt = conn.prepareStatement(selectSql);
			selectPstmt.setString(1, currentUser);
			ResultSet selectRs = selectPstmt.executeQuery();
			String userRole = "";
			String userPassword = "";
			if (selectRs.next()) {
				userRole = selectRs.getString("mrole");
				userPassword = selectRs.getString("mpassword");
			}

			if (rs.next()) {
				String writeId = rs.getString("mid");

				// 로그인한 사용자와 작성자 비교
				if ("ROLE_ADMIN".equals(userRole)) {
					// 관리자는 비밀번호 없이 수정 가능
					System.out.println("관리자 권한으로 수정합니다.");
					updateBoard(bno);
				} else if (currentUser != null && currentUser.equals(writeId)) {
					// 일반 사용자는 비밀번호 확인 후 수정 가능
					System.out.print("비밀번호를 입력해 주세요: ");
					String inputPassword = sc.nextLine();

					if (inputPassword.equals(userPassword)) {
						updateBoard(bno);
					} else {
						System.out.println("비밀번호가 일치하지 않습니다.");
					}
				} else {
					System.out.println("본인이 작성한 게시물만 수정할 수 있습니다.");
				}
			} else {
				System.out.println("해당 번호의 게시물이 존재하지 않습니다.");
			}

			selectPstmt.close();
			selectRs.close();
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 게시물 번호를 받아 수정 로직은 updateBoard()로 넘김
	public static void BoardUpdate(int bno) {
		try {
			// 현재 사용자의 권한 및 비밀번호 가져오기
			String selectSql = "SELECT mrole, mpassword FROM member WHERE mid = ?";
			PreparedStatement selectPstmt = conn.prepareStatement(selectSql);
			selectPstmt.setString(1, currentUser);
			ResultSet selectRs = selectPstmt.executeQuery();
			String userRole = "";
			String userPassword = "";
			if (selectRs.next()) {
				userRole = selectRs.getString("mrole");
				userPassword = selectRs.getString("mpassword");
			}

			// 작성자의 정보 가져오기
			String getWriterSql = "SELECT mid FROM board WHERE bno = ? AND benabled = 1";
			PreparedStatement getWriterPstmt = conn.prepareStatement(getWriterSql);
			getWriterPstmt.setInt(1, bno);
			ResultSet rs = getWriterPstmt.executeQuery();

			if (rs.next()) {
				String writeId = rs.getString("mid");

				// 로그인한 사용자와 작성자 비교
				if ("ROLE_ADMIN".equals(userRole)) {
					// 관리자는 비밀번호 없이 수정 가능
					System.out.println("관리자 권한으로 수정합니다.");
					updateBoard(bno);
				} else if (currentUser != null && currentUser.equals(writeId)) {
					// 일반 사용자는 비밀번호 확인 후 수정 가능
					System.out.print("비밀번호를 입력해 주세요: ");
					String inputPassword = sc.nextLine();

					if (inputPassword.equals(userPassword)) {
						updateBoard(bno);
					} else {
						System.out.println("비밀번호가 일치하지 않습니다.");
					}
				} else {
					System.out.println("본인이 작성한 게시물만 수정할 수 있습니다.");
				}
			} else {
				System.out.println("해당 번호의 게시물이 존재하지 않습니다.");
			}

			selectPstmt.close();
			selectRs.close();
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 게시물 수정로직
	private static void updateBoard(int bno) throws SQLException {
		System.out.println("새 제목: ");
		String btitle = sc.nextLine();
		System.out.println("새 내용: ");
		String bcontent = sc.nextLine();

		// 현재 시간으로 수정일자 저장
		Timestamp now = new Timestamp(System.currentTimeMillis());

		String sql = "UPDATE board SET btitle = ?, bcontent = ?, bupdatedat = ? WHERE bno = ? AND benabled = 1";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, btitle);
		pstmt.setString(2, bcontent);
		pstmt.setTimestamp(3, now); // 수정 시간 기록
		pstmt.setInt(4, bno);

		int rowsAffected = pstmt.executeUpdate();
		if (rowsAffected > 0) {
			System.out.println("게시물이 수정되었습니다.");
		} else {
			System.out.println("해당 번호의 게시물이 존재하지 않거나 수정할 수 없습니다.");
		}

		pstmt.close();
	}

	// 게시물 비활성화(삭제) 기능
	public static void BoardDelete() {
	    System.out.println("[게시물 삭제]");
	    System.out.print("bno: ");
	    int bno = Integer.parseInt(sc.nextLine());

	    System.out.print("비밀번호: ");
	    String inputMpassword = sc.nextLine();

	    // 사용자 권한 가져오기
	    String userRole = "";

	    try {
	        // JDBC 드라이버 등록
	    	Class.forName("oracle.jdbc.OracleDriver");
	    	Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/xe", "user01", "1004");

	        // 사용자 권한 가져오기
	        String roleQuery = "SELECT mrole FROM member WHERE mid = ?";
	        PreparedStatement roleStmt = conn.prepareStatement(roleQuery);
	        roleStmt.setString(1, currentUser); // 현재 로그인된 사용자 ID를 설정
	        ResultSet roleRs = roleStmt.executeQuery();

	        if (roleRs.next()) {
	            userRole = roleRs.getString("mrole");
	        }
	        roleRs.close();
	        roleStmt.close();

	        // 저장 프로시저 호출
	        java.sql.CallableStatement cstmt = conn.prepareCall("{call BoardDeleteProc(?, ?, ?, ?, ?)}");
	        cstmt.setInt(1, bno);
	        cstmt.setString(2, currentUser);
	        cstmt.setString(3, inputMpassword);
	        cstmt.setString(4, userRole);
	        cstmt.registerOutParameter(5, Types.VARCHAR);

	        cstmt.execute();
	        String result = cstmt.getString(5);
	        System.out.println(result);

	        cstmt.close();
	        conn.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	public static void main(String[] args) {
		System.out.println("--------------------------------");
		System.out.println("           미니 프로젝트            ");
		System.out.println("--------------------------------");
		// 데이터베이스 연결 설정
		setupDatabase();

		// 선택적으로 메인 메뉴 시작
		miniproject1_1 m1 = new miniproject1_1();
		m1.mainMenu();

	}
}