package com.techacademy.constants;

// エラーメッセージ定義
public enum ErrorKinds {

    // エラー内容
    // 空白チェックエラー
    BLANK_ERROR,
    // 半角英数字チェックエラー
    HALFSIZE_ERROR,
    // 桁数(8桁~16桁以外)チェックエラー
    RANGECHECK_ERROR,
    // 重複チェックエラー(例外あり)
    DUPLICATE_EXCEPTION_ERROR,
    // 重複チェックエラー(例外なし)
    DUPLICATE_ERROR,
    // ログイン中削除チェックエラー
    LOGINCHECK_ERROR,
    // 日付チェックエラー
    DATECHECK_ERROR,
    // チェックOK
    CHECK_OK,
    // 正常終了
    SUCCESS,
	//データが存在しない場合エラー
	NOT_FOUND_ERROR,
	//日付が空の場合のエラー
	REPORT_DATE_BLANK_ERROR,
	// タイトルが空の場合のエラー
	TITLE_BLANK_ERROR,
	// タイトルの文字数チェック用エラー
	TITLE_LENGTH_ERROR,
	// 内容が空の場合のエラー
	CONTENT_BLANK_ERROR,
	// 内容の文字数チェック用エラー
	CONTENT_LENGTH_ERROR;
	

}
