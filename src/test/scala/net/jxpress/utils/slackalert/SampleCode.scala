package net.jxpress.utils.slackalert

/** slackalert サンプルコード
  *
  */
object SampleCode extends App {

  import scala.concurrent.ExecutionContext.Implicits.global

  /** 単純な例：１つのクラスで簡単なアラートをslackに送る場合
    *
    */
  class SimpleApp extends SlackAlert {

    // 通知先のチャンネルやURLはこの変数をoverrideすることで設定する
    override val defaultAlert: Alert = Alert(
      text = "サンプル1",
      channel = "#test",
      url = "$your_webhook_url",
      username = "$yourName",
      icon_emoji = ":your_icon:")

    def hello(): Unit = {
      // text以外はdefaultAlertの設定でslackに通知する
      alert("hello メソッドが呼ばれたよ")
      println("hello!")
    }
  }

  /** よくある例:  開発版と本番で通知チャンネルをわける
    *
    */
  class StandardApp extends SlackAlert {
    override val defaultAlert: Alert = Alert(
      text = "開発版",
      channel = "#dev_channel",
      url = "$dev_channel_webhook_url",
      username = "$yourName",
      icon_emoji = ":dev_icon:")

    val devAlert = defaultAlert

    val prodAlert = Alert(
      text = "本番",
      channel = "#prod_channel",
      url = "$prod_channel_webhook_url",
      username = "$yourName",
      icon_emoji = ":prod_icon:")


    /** 共通のアラート変換処理をオーバーライド
      * これで alertメソッドを呼び出すと、この変換処理が適用される
      * オーバーライドしなかったら、デフォルトは無変換処理になります。
      */
    override val commonAlertConversion : Alert => Alert  = {
      alert =>
        sys.env.getOrElse("ENV", "dev") match {
          case "prod" =>
            prodAlert.copy(text = "本番:" + alert.text)
          case "dev" =>
            devAlert.copy(text = "開発版:" + alert.text)
        }
    }

    def hello(): Unit = {
      alert("hello!")
    }

    def bye(): Unit = {
      alert("bye!")
    }

    def yes(num: Int) : Unit = {
      val yesAlert = Alert(
        text = "これだけ個別処理",
        channel = "#yes_channel",
        url = "$yes_channel_webhook_url",
        username = "$yourName",
        icon_emoji = ":yes_icon:")

      // 個別に違うチャンネルに通知したいときなどは、以下のAPIを使う　
      // このアラートだけは上記alertを変換なしで通知
      alert(yesAlert) {
        a => a
      }
    }
  }


  /** 複数のクラスや、プロジェクト全体で共通で使いたい場合は trait を使って共通の設定を実装し、
    * 使用したいクラスやobjectにmix-inさせると便利
    *
    */
  trait MyAppSlackAlert extends SlackAlert {
    override val defaultAlert: Alert = Alert(
      text = "開発版",
      channel = "#dev_channel",
      url = "$dev_channel_webhook_url",
      username = "$yourName",
      icon_emoji = ":dev_icon:")

    val devAlert = defaultAlert
    val prodAlert = Alert(
      text = "本番",
      channel = "#prod_channel",
      url = "$prod_channel_webhook_url",
      username = "$yourName",
      icon_emoji = ":prod_icon:")

    override val commonAlertConversion : Alert => Alert  = {
      alert =>
        sys.env.getOrElse("ENV", "dev") match {
          case "prod" =>
            prodAlert.copy(text = "本番:" + alert.text)
          case "dev" =>
            devAlert.copy(text = "開発版:" + alert.text)
        }
    }
  }
}
