package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.EmployeeRepository;

@Service
public class EmployeeService {
	private Report report;
	private final EmployeeRepository employeeRepository;
	private final PasswordEncoder passwordEncoder;
	private final ReportService reportService;  // ReportServiceの追加


	@Autowired
	public EmployeeService(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder, ReportService reportService) {
		this.employeeRepository = employeeRepository;
		this.passwordEncoder = passwordEncoder;
		this.reportService = reportService;  // ReportServiceの初期化
	}

	// パスワードをハッシュ化するメソッド
	public String hashPassword(String rawPassword) {
		return passwordEncoder.encode(rawPassword);
	}
		
	// 従業員保存
	@Transactional
	public ErrorKinds save(Employee employee) {
		// パスワードチェック
		ErrorKinds result = employeePasswordCheck(employee);
		if (ErrorKinds.CHECK_OK != result) {
			return result;
		}

		// 従業員番号重複チェック
		if (employeeRepository.existsById(employee.getCode())) {
			return ErrorKinds.DUPLICATE_ERROR;
		}

		employee.setDeleteFlg(false);

		LocalDateTime now = LocalDateTime.now();
		employee.setCreatedAt(now);
		employee.setUpdatedAt(now);
		// パスワードのハッシュ化
		employee.setPassword(passwordEncoder.encode(employee.getPassword()));

		employeeRepository.save(employee);
		return ErrorKinds.SUCCESS;
	}

	// 従業員更新
	@Transactional
	public ErrorKinds updateEmployee(Employee employee) {
		Employee beforeEmployee = findByCode(employee.getCode());
		if (beforeEmployee == null) {
			// FIXME 指定されたデーターが存在しない時、エラーを返す
			return ErrorKinds.NOT_FOUND_ERROR;
		}
		// パスワードが空の場合、既存のパスワードを保持
		//employee.setPassword(beforeEmployee.getPassword());

		employee.setCreatedAt(beforeEmployee.getCreatedAt());
		employee.setUpdatedAt(LocalDateTime.now());

		employeeRepository.save(employee);
		return ErrorKinds.SUCCESS;
	}

	// 従業員削除
	//@Transactional
	//public ErrorKinds delete(String code, UserDetail userDetail) {
		// 自分を削除しようとした場合はエラーメッセージを表示
		//if (code.equals(userDetail.getEmployee().getCode())) {
			//return ErrorKinds.LOGINCHECK_ERROR;
		//}
		//Employee employee = findByCode(code);
		//LocalDateTime now = LocalDateTime.now();
		//employee.setUpdatedAt(now);
		//employee.setDeleteFlg(true);

		//return ErrorKinds.SUCCESS;
	//}

	// 従業員削除
    @Transactional
    public ErrorKinds delete(String code, UserDetail userDetail) {
        // 自分を削除しようとした場合はエラーメッセージを表示
        if (code.equals(userDetail.getEmployee().getCode())) {
            return ErrorKinds.LOGINCHECK_ERROR;
        }

        Employee employee = findByCode(code);
        if (employee == null) {
            return ErrorKinds.NOT_FOUND_ERROR;
        }

        // 削除対象の従業員に紐づいている日報情報の削除
        List<Report> reportList = reportService.findByEmployee(employee);
        for (Report report : reportList) {
            reportService.delete(report.getId());
        }

        // 従業員情報の削除（論理削除）
        employee.setUpdatedAt(LocalDateTime.now());
        employee.setDeleteFlg(true);
        employeeRepository.save(employee);

        return ErrorKinds.SUCCESS;
    }

	// 従業員一覧表示処理
	public List<Employee> findAll() {
		return employeeRepository.findAll();
	}

	// 1件を検索
	public Employee findByCode(String code) {
		// findByIdで検索
		Optional<Employee> option = employeeRepository.findById(code);
		// 取得できなかった場合はnullを返す
		Employee employee = option.orElse(null);
		return employee;
	}

	// 従業員パスワードチェック
	private ErrorKinds employeePasswordCheck(Employee employee) {
		// 従業員パスワードの半角英数字チェック処理
		if (isHalfSizeCheckError(employee)) {
			return ErrorKinds.HALFSIZE_ERROR;
		}

		// 従業員パスワードの8文字～16文字チェック処理
		if (isOutOfRangePassword(employee)) {
			return ErrorKinds.RANGECHECK_ERROR;
		}

		return ErrorKinds.CHECK_OK;
	}
	
	// 従業員パスワードの半角英数字チェック処理
	private boolean isHalfSizeCheckError(Employee employee) {
		// 半角英数字チェック
		Pattern pattern = Pattern.compile("^[A-Za-z0-9]+$");
		Matcher matcher = pattern.matcher(employee.getPassword());
		return !matcher.matches();
	}

	// 従業員パスワードの8文字～16文字チェック処理
	public boolean isOutOfRangePassword(Employee employee) {
		// 桁数チェック
		int passwordLength = employee.getPassword().length();
		return passwordLength < 8 || 16 < passwordLength;
	}
}
