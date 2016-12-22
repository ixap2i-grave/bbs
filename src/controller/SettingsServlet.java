package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import beans.Branch;
import beans.Position;
import beans.User;
import exception.NoRowsUpdatedRuntimeException;
import service.BranchService;
import service.PositionService;
import service.UserService;

@WebServlet(urlPatterns = { "/settings" })
@MultipartConfig(maxFileSize = 100000)
public class SettingsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();

		List<Branch> branches = new BranchService().getBranches();
		session.setAttribute("branches", branches);

		List<Position> positions = new PositionService().getPositions();
		session.setAttribute("positions", positions);

		//idに数字以外が入ったときの バリデーション
		//ユーザー一覧に戻し方がわからない


		try {
			int id = Integer.parseInt(request.getParameter("id"));
			User editUser = new UserService().getUser(id);

			if(editUser != null) {
				session.setAttribute("editUser", editUser);
				request.getRequestDispatcher("settings.jsp").forward(request, response);

			} else {
				List<String> messages = new ArrayList<>();
				messages.add("不正なIDが取得されました");
				session.setAttribute("errorMessage", messages);
				response.sendRedirect("users");
			}
		} catch(NumberFormatException e) {
			List<String> messages = new ArrayList<>();
			messages.add("不正なIDが取得されました");
			session.setAttribute("errorMessage", messages);
			response.sendRedirect("users");
		}

	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		List<String> messages = new ArrayList<>();

		HttpSession session = request.getSession();

		User editUser = getEditUser(request);
		session.setAttribute("editUser", editUser);

		if (isValid(request, messages) == false) {
			session.setAttribute("errorMessages", messages);
			request.getRequestDispatcher("/settings.jsp").forward(request , response);
			//値をsettingsに投げる
			return;
		}

		try {
			new UserService().update(editUser);

			User loginUser = (User) session.getAttribute("loginUser");
			if(editUser.getId() == loginUser.getId()){
				User updatedLoginUser = new UserService().getUser(loginUser.getId());
				session.setAttribute("loginUser", updatedLoginUser);//12/21付けたし　ログインユーザーの更新
			}

		} catch (NoRowsUpdatedRuntimeException e) {
			session.removeAttribute("editUser");
			messages.add("他の人によって更新されています。最新のデータを表示しました。データを確認してください。");
			session.setAttribute("errorMessages", messages);
		}
		session.removeAttribute("editUser");
		response.sendRedirect("users");
	}

	private User getEditUser(HttpServletRequest request)
			throws IOException, ServletException {

		HttpSession session = request.getSession();
		User editUser = (User) session.getAttribute("editUser");

		String password = request.getParameter("password");

		editUser.setName(request.getParameter("name"));
		editUser.setLoginId(request.getParameter("account"));
		editUser.setPassword(password);
		editUser.setBranchId(Integer.parseInt(request.getParameter("branchId")));
		editUser.setPostId(Integer.parseInt(request.getParameter("positionId")));

		return editUser;
	}


	private boolean isValid(HttpServletRequest request, List<String> messages) {

		String id = request.getParameter("id");
		int userId = Integer.parseInt(id);
		String name = request.getParameter("name");
		String loginId = request.getParameter("account");
		String password = request.getParameter("password");
		String passwordConfirm = request.getParameter("password_confirm");
		String branchId = request.getParameter("branchId");
		String postId = request.getParameter("positionId");

		//userIdを比較する
		User loginUser = new UserService().setLoginId(loginId);

		if(StringUtils.isBlank(name)){
			messages.add("名前を入力してください");
		}
		if (10 < name.length()) {
			messages.add("10文字以内で入力してください:名前");
		}
		if (StringUtils.isBlank(loginId) == true) {
			messages.add("ログインIDを入力してください");
		}
		if (6 > loginId.length() || loginId.length() > 20) {
			messages.add("6文字以上20文字以内で入力してください：ログインID");
		}
		if(loginUser != null && loginUser.getId() != userId){
			messages.add("このログインIDは使用済みです");
		}
		if(!StringUtils.isBlank(password) || !StringUtils.isBlank(passwordConfirm)){
			if (StringUtils.isBlank(password) == true) {
				messages.add("パスワードを入力してください");
			}
			if (!(password.equals(passwordConfirm))) {
				messages.add("パスワードを確認してください");
			}
			if (6 > password.length() || password.length() > 255) {
				messages.add("6文字以上255文字以下で入力してください：パスワード");
			}
		}
		if(isNumber(branchId) == false) {
			messages.add("数字を入力してください");
		}
		if(isNumber(postId) == false){
			messages.add("数字を入力してください");
		}

		return messages.size() == 0;
	}

	public boolean isNumber(String num) {
		try {
			Integer.parseInt(num);
			return true;
		} catch(NumberFormatException e) {
			return false;
		}
	}

}
