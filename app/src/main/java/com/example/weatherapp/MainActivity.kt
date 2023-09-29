package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Website.URL
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //0)準備　(APIキーと,URL(の基本部分)を定義）
        val apiKey = "1be276ac5cbb12362fb251047a74367b"
        val mainUrl = "https://api.openweathermap.org/data/2.5/weather?lang=ja"


        //0)準備 viewを取得
        val btnTokyo: Button = findViewById(R.id.btnTokyo)
        val btnokinawa : Button = findViewById(R.id.btnOkinawa)
        val tvCityName: TextView = findViewById(R.id.tvCityName)
        val tvCityWeather: TextView = findViewById(R.id.tvCityWeather)
        val tvmax: TextView = findViewById(R.id.tvmax)
        val tvMin : TextView = findViewById(R.id.tvMin)
        val btnclear: Button = findViewById(R.id.btnclear)

        //1)btnaTokyoが押されたら
        btnTokyo.setOnClickListener{
            //[1-1]東京のお天気URLを取得して
            val weatherUrl = "$mainUrl&q=Tokyo&appid=$apiKey"

            //[1-2]そのURLを元に得られた情報の結果を表示
            //2)コルーチンを作る→3)HTTP通信（ワーカースレッド）→4)結果を表示（メインスレッド）
            weatherTask(weatherUrl)
        }
        btnokinawa.setOnClickListener {
            val weatherUrl = "$mainUrl&q=Okinawa&appid=$apiKey"

            weatherTask(weatherUrl)
        }
        btnclear.setOnClickListener {
            tvCityName.text = "都市名"
            tvCityWeather.text = "都市の天気"
            tvmax.text = "最高気温"
            tvMin.text = "最低気温"
        }
    }

    //2)WeatherTaskの中身
    private fun weatherTask(weatherUrl: String){
        //コルーチンスコープを作る
        lifecycleScope.launch {
            //3)HTTP通信（ワーカースレッド）
            val result = weatherBackgroundTask(weatherUrl)

            //4) 3を受けて、お天気でーた（JSONデータ）を表示（UIスレッド）
            weatherJsonTask(result)
        }
    }

    //3)HTTP通信（ワーカースレッド）の中身(suspend=中断する可能性がある関数につける）
    private suspend fun weatherBackgroundTask(weatherUrl: String):String{
        //withContext=スレッドを分離する、Dispatchers.IO=ワーカースレッド
        val response = withContext(Dispatchers.IO){
            //天気情報サービスから取得した結構を格納する変数
            var httpResult = ""

            //try{エラーがあるかもしれない処理を実行}catch{エラーがあった場合の処理}
            try {
                //ただのURL文字列をURLオブジェクトに変換
                val urlObj = URL(weatherUrl)
                //アクセスしたAPIから情報を取得
                //テキストファイルを読み込むクラス(文字コードを読めるようにする）
                val br = BufferedReader(InputStreamReader(urlObj.openStream()))

                //読み込んだデータを文字列に変換して代入
                httpResult = br.readText()
            }catch (e: IOException){//IOException＝例外管理するクラス
                //エラーがあった場合の処理
                e.printStackTrace()
            }catch (e:JSONException){//JSONデータ構造に問題が発生した場合の例外
                e.printStackTrace()
            }
            //HTTP接続の結果、取得したJSON文字列httpResultを戻り値とする
            return@withContext httpResult
        }

        return response
    }

    //4) 3を受けて、お天気でーた（JSONデータ）を表示（UIスレッド）の中身
    private fun weatherJsonTask(result:String){
        val tvCityName: TextView = findViewById(R.id.tvCityName)
        val tvCityWeather: TextView = findViewById(R.id.tvCityWeather)
        val tvmax: TextView = findViewById(R.id.tvmax)
        val tvMin : TextView = findViewById(R.id.tvMin)

        //3で取得したJSON文字列をJSONオブジェクトに変換

        val jsonObj = JSONObject(result)

        // jsonオブジェクトの、都市名をキーを取得して、TVに代入して表示
        val cityName = jsonObj.getString("name")
        tvCityName.text = cityName

        //jsonオブジェクトの天気情報JSON配列オブジェクトを取得
        val weatherJSONArray = jsonObj.getJSONArray("weather")
        //天気情報JSON配列の0番目のJSONオブジェクトを取得
        val weatherJSON = weatherJSONArray.getJSONObject(0)
        //お天気の説明
        val weather = weatherJSON.getString("description")
        //TextViewに表示
        tvCityWeather.text = weather

        //JSONオブジェクトのmainキーを取得
        val main = jsonObj.getJSONObject("main")
        //tvmaxに最高気温を表示

        tvmax.text = "最高気温:${main.getInt("temp_max")-273}℃"
        //tvminに最低気温を表示
        tvMin.text = "最低気温:${main.getInt("temp_min")-273}℃"

    }
}