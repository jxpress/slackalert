package net.jxpress.utils.slackalert

import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization

import scala.concurrent.{ExecutionContext, Future}
import scalaj.http.{Http, HttpOptions}

/** アラートのケースクラス
  *
  * @param text アラートメッセージ本体
  * @param channel アラートを通知するslack channel
  * @param url 通知を送るURL
  * @param username アラートを投げるときのユーザ名
  * @param icon_emoji アイコン
  * @param link_names linkやnameを有効にするスイッチ(default 1で有効)
  */
case class Alert(text: String,
                 channel: String,
                 url: String,
                 username: String,
                 icon_emoji: String,
                 link_names: Int = 1 )

trait SlackAlert {

  /** デフォルトのアラート
    *
    */
  val defaultAlert: Alert

  /** 共通変換関数
    *
    * 送るアラートに対して、共通で変換処理を行う場合は、この変数をoverrideする。デフォルトは無変換　
    *
    */
  val commonAlertConversion : Alert => Alert = { a =>  a}

  /** 指定したメッセージでslackにアラートを送信し、レスポンスを返す
    *
    * message以外は　defaultAlertの設定が利用され、変換処理はcommonAlertConversionが利用される。
    *
    * @param text アラートメッセージ
    * @param ec ExecutionContext
    */
  def alert(text: String)(implicit ec: ExecutionContext) : Future[(Int, String)] = alert(defaultAlert.copy(text = text))(commonAlertConversion)

  /** 指定したアラートでslackに送信し、そのレスポンスを返す
    *
    * @param alert アラート
    * @param f alert変換関数。
    * @param ec ExecutionContext
    */
  def alert(alert: Alert)(f: Alert => Alert)(implicit ec: ExecutionContext): Future[(Int, String)]  = {
    implicit val formats = Serialization.formats(NoTypeHints)
    val json = Serialization.write(f(alert))
    Future {
      val response = Http(alert.url).option(HttpOptions.allowUnsafeSSL).header("Content-Type", "application/json").postData(json).asString
      response.code -> response.body
    }
  }
}
