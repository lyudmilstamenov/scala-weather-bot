import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar.mock
import org.mockito.ArgumentMatchers.any
import org.mockito.{ArgumentCaptor, ArgumentMatchers, Mockito}
import parsing.*

import java.io.FileInputStream

class JsonParserTest extends AnyFlatSpec with Matchers :
  val currentJsonResponse =
    "{\"location\":{\"name\":\"Sofia\",\"region\":\"Grad Sofiya\",\"country\":\"Bulgaria\",\"lat\":42.68,\"lon\":23.32,\"tz_id\":\"Europe/Sofia\",\"localtime_epoch\":1656934913,\"localtime\":\"2022-07-04 14:41\"},\"current\":{\"last_updated_epoch\":1656934200,\"last_updated\":\"2022-07-04 14:30\",\"temp_c\":30.0,\"temp_f\":86.0,\"is_day\":1,\"condition\":{\"text\":\"Partly cloudy\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/day/116.png\",\"code\":1003},\"wind_mph\":2.2,\"wind_kph\":3.6,\"wind_degree\":75,\"wind_dir\":\"ENE\",\"pressure_mb\":1018.0,\"pressure_in\":30.06,\"precip_mm\":0.0,\"precip_in\":0.0,\"humidity\":43,\"cloud\":50,\"feelslike_c\":29.4,\"feelslike_f\":84.9,\"vis_km\":10.0,\"vis_miles\":6.0,\"uv\":8.0,\"gust_mph\":6.7,\"gust_kph\":10.8}}"
  val headerExample: Map[String, Seq[String]] = Map(
    "cdn-proxyver" -> List("1.02"),
    "cdn-requestid" -> List("f079dd597070e8e654eba0717d98636d"),
    "server" -> List("BunnyCDN-DE-632"),
    "cache-control" -> List("public", "max-age=180"),
    "cdn-edgestorageid" -> List("756"),
    "date" -> List("Sat", "09 Jul 2022 09:58:02 GMT"),
    "cdn-cache" -> List("EXPIRED"),
    "cdn-status" -> List("200"),
    "cdn-pullzone" -> List("93447"),
    "vary" -> List("Accept-Encoding"),
    "content-encoding" -> List("gzip"),
    "cdn-cachedat" -> List("07/09/2022 09:58:02"),
    "cdn-requestpullsuccess" -> List("True"),
    "content-type" -> List("application/json"),
    "cdn-requestpullcode" -> List("200"),
    "cdn-uid" -> List("8fa3a04a-75d9-4707-8056-b7b33c8ac7fe"),
    "transfer-encoding" -> List("chunked"),
    "cdn-requestcountrycode" -> List("BG"),
    "connection" -> List("keep-alive")
  )
  val TEST_FILE_PATH: String = "src/test/resources/test-logger.xlsx"

  "parsing.JsonParsedCurrent" should "create the class and write in the table correctly" in {
    val current = JsonParsedCurrent(currentJsonResponse, headerExample)
    val testFile = TEST_FILE_PATH
    current.writeDataInTable(testFile)

    val inputStream = new FileInputStream(testFile)
    val workbook = new XSSFWorkbook(inputStream)
    val sheet = workbook.getSheet(current.command)

    val lastRowNum = sheet.getLastRowNum
    val currentRow = sheet.getRow(lastRowNum)

    val getDateResponse = current.header.getOrElse("date", "unidentified")

    currentRow.getCell(0).getNumericCellValue shouldBe lastRowNum.toDouble
    currentRow.getCell(1).getStringCellValue shouldBe getDateResponse.toString.substring(4)
    currentRow.getCell(2).getStringCellValue shouldBe current.rawValue
    currentRow.getCell(3).getStringCellValue shouldBe current.command

    inputStream.close()
    workbook.close()
  }

  val forecastJsonResponse =
    "{\"location\":{\"name\":\"Sofia\",\"region\":\"Grad Sofiya\",\"country\":\"Bulgaria\",\"lat\":42.68,\"lon\":23.32,\"tz_id\":\"Europe/Sofia\",\"localtime_epoch\":1657363149,\"localtime\":\"2022-07-09 13:39\"},\"current\":{\"last_updated_epoch\":1657362600,\"last_updated\":\"2022-07-09 13:30\",\"temp_c\":19.0,\"temp_f\":66.2,\"is_day\":1,\"condition\":{\"text\":\"Moderate rain\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/day/302.png\",\"code\":1189},\"wind_mph\":6.9,\"wind_kph\":11.2,\"wind_degree\":320,\"wind_dir\":\"NW\",\"pressure_mb\":1016.0,\"pressure_in\":30.0,\"precip_mm\":0.8,\"precip_in\":0.03,\"humidity\":73,\"cloud\":100,\"feelslike_c\":19.0,\"feelslike_f\":66.2,\"vis_km\":10.0,\"vis_miles\":6.0,\"uv\":4.0,\"gust_mph\":12.3,\"gust_kph\":19.8},\"forecast\":{\"forecastday\":[{\"date\":\"2022-07-09\",\"date_epoch\":1657324800,\"day\":{\"maxtemp_c\":17.9,\"maxtemp_f\":64.2,\"mintemp_c\":10.2,\"mintemp_f\":50.4,\"avgtemp_c\":14.8,\"avgtemp_f\":58.7,\"maxwind_mph\":8.5,\"maxwind_kph\":13.7,\"totalprecip_mm\":22.1,\"totalprecip_in\":0.87,\"avgvis_km\":8.5,\"avgvis_miles\":5.0,\"avghumidity\":88.0,\"daily_will_it_rain\":1,\"daily_chance_of_rain\":98,\"daily_will_it_snow\":0,\"daily_chance_of_snow\":0,\"condition\":{\"text\":\"Heavy rain\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/day/308.png\",\"code\":1195},\"uv\":3.0},\"astro\":{\"sunrise\":\"05:57 AM\",\"sunset\":\"09:06 PM\",\"moonrise\":\"04:28 PM\",\"moonset\":\"01:57 AM\",\"moon_phase\":\"Waxing Gibbous\",\"moon_illumination\":\"70\"},\"hour\":[{\"time_epoch\":1657314000,\"time\":\"2022-07-09 00:00\",\"temp_c\":13.2,\"temp_f\":55.8,\"is_day\":0,\"condition\":{\"text\":\"Light rain\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/night/296.png\",\"code\":1183},\"wind_mph\":3.6,\"wind_kph\":5.8,\"wind_degree\":300,\"wind_dir\":\"WNW\",\"pressure_mb\":1016.0,\"pressure_in\":29.99,\"precip_mm\":0.8,\"precip_in\":0.03,\"humidity\":92,\"cloud\":100,\"feelslike_c\":13.2,\"feelslike_f\":55.8,\"windchill_c\":13.2,\"windchill_f\":55.8,\"heatindex_c\":13.2,\"heatindex_f\":55.8,\"dewpoint_c\":11.9,\"dewpoint_f\":53.4,\"will_it_rain\":1,\"chance_of_rain\":91,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":9.0,\"vis_miles\":5.0,\"gust_mph\":7.2,\"gust_kph\":11.5,\"uv\":1.0},{\"time_epoch\":1657317600,\"time\":\"2022-07-09 01:00\",\"temp_c\":12.9,\"temp_f\":55.2,\"is_day\":0,\"condition\":{\"text\":\"Light rain shower\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/night/353.png\",\"code\":1240},\"wind_mph\":4.5,\"wind_kph\":7.2,\"wind_degree\":310,\"wind_dir\":\"NW\",\"pressure_mb\":1015.0,\"pressure_in\":29.98,\"precip_mm\":0.7,\"precip_in\":0.03,\"humidity\":94,\"cloud\":67,\"feelslike_c\":12.6,\"feelslike_f\":54.7,\"windchill_c\":12.6,\"windchill_f\":54.7,\"heatindex_c\":12.9,\"heatindex_f\":55.2,\"dewpoint_c\":11.9,\"dewpoint_f\":53.4,\"will_it_rain\":1,\"chance_of_rain\":89,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":10.0,\"vis_miles\":6.0,\"gust_mph\":8.9,\"gust_kph\":14.4,\"uv\":1.0},{\"time_epoch\":1657321200,\"time\":\"2022-07-09 02:00\",\"temp_c\":12.7,\"temp_f\":54.9,\"is_day\":0,\"condition\":{\"text\":\"Moderate rain\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/night/302.png\",\"code\":1189},\"wind_mph\":4.9,\"wind_kph\":7.9,\"wind_degree\":313,\"wind_dir\":\"NW\",\"pressure_mb\":1015.0,\"pressure_in\":29.98,\"precip_mm\":3.9,\"precip_in\":0.15,\"humidity\":95,\"cloud\":78,\"feelslike_c\":12.2,\"feelslike_f\":54.0,\"windchill_c\":12.2,\"windchill_f\":54.0,\"heatindex_c\":12.7,\"heatindex_f\":54.9,\"dewpoint_c\":11.9,\"dewpoint_f\":53.4,\"will_it_rain\":1,\"chance_of_rain\":92,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":7.0,\"vis_miles\":4.0,\"gust_mph\":9.8,\"gust_kph\":15.8,\"uv\":1.0},{\"time_epoch\":1657324800,\"time\":\"2022-07-09 03:00\",\"temp_c\":12.6,\"temp_f\":54.7,\"is_day\":0,\"condition\":{\"text\":\"Light rain shower\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/night/353.png\",\"code\":1240},\"wind_mph\":4.3,\"wind_kph\":6.8,\"wind_degree\":310,\"wind_dir\":\"NW\",\"pressure_mb\":1015.0,\"pressure_in\":29.97,\"precip_mm\":0.4,\"precip_in\":0.02,\"humidity\":95,\"cloud\":81,\"feelslike_c\":12.3,\"feelslike_f\":54.1,\"windchill_c\":12.3,\"windchill_f\":54.1,\"heatindex_c\":12.6,\"heatindex_f\":54.7,\"dewpoint_c\":11.9,\"dewpoint_f\":53.4,\"will_it_rain\":1,\"chance_of_rain\":80,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":10.0,\"vis_miles\":6.0,\"gust_mph\":8.3,\"gust_kph\":13.3,\"uv\":1.0},{\"time_epoch\":1657328400,\"time\":\"2022-07-09 04:00\",\"temp_c\":12.5,\"temp_f\":54.5,\"is_day\":0,\"condition\":{\"text\":\"Light rain shower\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/night/353.png\",\"code\":1240},\"wind_mph\":3.6,\"wind_kph\":5.8,\"wind_degree\":310,\"wind_dir\":\"NW\",\"pressure_mb\":1015.0,\"pressure_in\":29.97,\"precip_mm\":0.4,\"precip_in\":0.02,\"humidity\":95,\"cloud\":82,\"feelslike_c\":12.4,\"feelslike_f\":54.3,\"windchill_c\":12.4,\"windchill_f\":54.3,\"heatindex_c\":12.5,\"heatindex_f\":54.5,\"dewpoint_c\":11.7,\"dewpoint_f\":53.1,\"will_it_rain\":0,\"chance_of_rain\":63,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":10.0,\"vis_miles\":6.0,\"gust_mph\":7.2,\"gust_kph\":11.5,\"uv\":1.0},{\"time_epoch\":1657332000,\"time\":\"2022-07-09 05:00\",\"temp_c\":12.8,\"temp_f\":55.0,\"is_day\":0,\"condition\":{\"text\":\"Light rain shower\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/night/353.png\",\"code\":1240},\"wind_mph\":2.9,\"wind_kph\":4.7,\"wind_degree\":330,\"wind_dir\":\"NNW\",\"pressure_mb\":1014.0,\"pressure_in\":29.96,\"precip_mm\":1.1,\"precip_in\":0.04,\"humidity\":94,\"cloud\":100,\"feelslike_c\":13.0,\"feelslike_f\":55.4,\"windchill_c\":13.0,\"windchill_f\":55.4,\"heatindex_c\":12.8,\"heatindex_f\":55.0,\"dewpoint_c\":11.8,\"dewpoint_f\":53.2,\"will_it_rain\":1,\"chance_of_rain\":81,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":10.0,\"vis_miles\":6.0,\"gust_mph\":5.8,\"gust_kph\":9.4,\"uv\":1.0},{\"time_epoch\":1657335600,\"time\":\"2022-07-09 06:00\",\"temp_c\":14.4,\"temp_f\":57.9,\"is_day\":1,\"condition\":{\"text\":\"Partly cloudy\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/day/116.png\",\"code\":1003},\"wind_mph\":3.1,\"wind_kph\":5.0,\"wind_degree\":6,\"wind_dir\":\"N\",\"pressure_mb\":1015.0,\"pressure_in\":29.96,\"precip_mm\":0.0,\"precip_in\":0.0,\"humidity\":92,\"cloud\":42,\"feelslike_c\":14.7,\"feelslike_f\":58.5,\"windchill_c\":14.7,\"windchill_f\":58.5,\"heatindex_c\":14.4,\"heatindex_f\":57.9,\"dewpoint_c\":13.2,\"dewpoint_f\":55.8,\"will_it_rain\":0,\"chance_of_rain\":0,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":10.0,\"vis_miles\":6.0,\"gust_mph\":5.1,\"gust_kph\":8.3,\"uv\":4.0},{\"time_epoch\":1657339200,\"time\":\"2022-07-09 07:00\",\"temp_c\":15.3,\"temp_f\":59.5,\"is_day\":1,\"condition\":{\"text\":\"Light rain shower\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/day/353.png\",\"code\":1240},\"wind_mph\":3.4,\"wind_kph\":5.4,\"wind_degree\":7,\"wind_dir\":\"N\",\"pressure_mb\":1015.0,\"pressure_in\":29.96,\"precip_mm\":0.4,\"precip_in\":0.02,\"humidity\":90,\"cloud\":67,\"feelslike_c\":15.3,\"feelslike_f\":59.5,\"windchill_c\":15.3,\"windchill_f\":59.5,\"heatindex_c\":15.3,\"heatindex_f\":59.5,\"dewpoint_c\":13.7,\"dewpoint_f\":56.7,\"will_it_rain\":1,\"chance_of_rain\":78,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":10.0,\"vis_miles\":6.0,\"gust_mph\":4.9,\"gust_kph\":7.9,\"uv\":4.0},{\"time_epoch\":1657342800,\"time\":\"2022-07-09 08:00\",\"temp_c\":16.4,\"temp_f\":61.5,\"is_day\":1,\"condition\":{\"text\":\"Partly cloudy\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/day/116.png\",\"code\":1003},\"wind_mph\":4.3,\"wind_kph\":6.8,\"wind_degree\":5,\"wind_dir\":\"N\",\"pressure_mb\":1015.0,\"pressure_in\":29.96,\"precip_mm\":0.0,\"precip_in\":0.0,\"humidity\":86,\"cloud\":50,\"feelslike_c\":16.4,\"feelslike_f\":61.5,\"windchill_c\":16.4,\"windchill_f\":61.5,\"heatindex_c\":16.4,\"heatindex_f\":61.5,\"dewpoint_c\":14.1,\"dewpoint_f\":57.4,\"will_it_rain\":0,\"chance_of_rain\":0,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":10.0,\"vis_miles\":6.0,\"gust_mph\":5.1,\"gust_kph\":8.3,\"uv\":5.0},{\"time_epoch\":1657346400,\"time\":\"2022-07-09 09:00\",\"temp_c\":16.9,\"temp_f\":62.4,\"is_day\":1,\"condition\":{\"text\":\"Light rain shower\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/day/353.png\",\"code\":1240},\"wind_mph\":5.4,\"wind_kph\":8.6,\"wind_degree\":9,\"wind_dir\":\"N\",\"pressure_mb\":1015.0,\"pressure_in\":29.96,\"precip_mm\":0.8,\"precip_in\":0.03,\"humidity\":83,\"cloud\":53,\"feelslike_c\":16.9,\"feelslike_f\":62.4,\"windchill_c\":16.9,\"windchill_f\":62.4,\"heatindex_c\":16.9,\"heatindex_f\":62.4,\"dewpoint_c\":14.0,\"dewpoint_f\":57.2,\"will_it_rain\":1,\"chance_of_rain\":98,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":10.0,\"vis_miles\":6.0,\"gust_mph\":6.9,\"gust_kph\":11.2,\"uv\":4.0},{\"time_epoch\":1657350000,\"time\":\"2022-07-09 10:00\",\"temp_c\":17.2,\"temp_f\":63.0,\"is_day\":1,\"condition\":{\"text\":\"Patchy rain possible\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/day/176.png\",\"code\":1063},\"wind_mph\":6.7,\"wind_kph\":10.8,\"wind_degree\":15,\"wind_dir\":\"NNE\",\"pressure_mb\":1015.0,\"pressure_in\":29.96,\"precip_mm\":0.7,\"precip_in\":0.03,\"humidity\":80,\"cloud\":73,\"feelslike_c\":17.2,\"feelslike_f\":63.0,\"windchill_c\":17.2,\"windchill_f\":63.0,\"heatindex_c\":17.2,\"heatindex_f\":63.0,\"dewpoint_c\":13.8,\"dewpoint_f\":56.8,\"will_it_rain\":1,\"chance_of_rain\":97,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":9.0,\"vis_miles\":5.0,\"gust_mph\":9.2,\"gust_kph\":14.8,\"uv\":4.0},{\"time_epoch\":1657353600,\"time\":\"2022-07-09 11:00\",\"temp_c\":17.5,\"temp_f\":63.5,\"is_day\":1,\"condition\":{\"text\":\"Moderate or heavy rain shower\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/day/356.png\",\"code\":1243},\"wind_mph\":6.7,\"wind_kph\":10.8,\"wind_degree\":12,\"wind_dir\":\"NNE\",\"pressure_mb\":1015.0,\"pressure_in\":29.97,\"precip_mm\":2.5,\"precip_in\":0.1,\"humidity\":80,\"cloud\":61,\"feelslike_c\":17.5,\"feelslike_f\":63.5,\"windchill_c\":17.5,\"windchill_f\":63.5,\"heatindex_c\":17.5,\"heatindex_f\":63.5,\"dewpoint_c\":13.9,\"dewpoint_f\":57.0,\"will_it_rain\":1,\"chance_of_rain\":97,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":7.0,\"vis_miles\":4.0,\"gust_mph\":10.5,\"gust_kph\":16.9,\"uv\":4.0},{\"time_epoch\":1657357200,\"time\":\"2022-07-09 12:00\",\"temp_c\":17.9,\"temp_f\":64.2,\"is_day\":1,\"condition\":{\"text\":\"Light rain shower\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/day/353.png\",\"code\":1240},\"wind_mph\":7.4,\"wind_kph\":11.9,\"wind_degree\":8,\"wind_dir\":\"N\",\"pressure_mb\":1015.0,\"pressure_in\":29.96,\"precip_mm\":0.8,\"precip_in\":0.03,\"humidity\":79,\"cloud\":64,\"feelslike_c\":17.9,\"feelslike_f\":64.2,\"windchill_c\":17.9,\"windchill_f\":64.2,\"heatindex_c\":17.9,\"heatindex_f\":64.2,\"dewpoint_c\":14.2,\"dewpoint_f\":57.6,\"will_it_rain\":1,\"chance_of_rain\":97,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":10.0,\"vis_miles\":6.0,\"gust_mph\":12.3,\"gust_kph\":19.8,\"uv\":4.0},{\"time_epoch\":1657360800,\"time\":\"2022-07-09 13:00\",\"temp_c\":17.7,\"temp_f\":63.9,\"is_day\":1,\"condition\":{\"text\":\"Light rain shower\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/day/353.png\",\"code\":1240},\"wind_mph\":8.1,\"wind_kph\":13.0,\"wind_degree\":0,\"wind_dir\":\"N\",\"pressure_mb\":1015.0,\"pressure_in\":29.96,\"precip_mm\":0.9,\"precip_in\":0.04,\"humidity\":80,\"cloud\":77,\"feelslike_c\":17.7,\"feelslike_f\":63.9,\"windchill_c\":17.7,\"windchill_f\":63.9,\"heatindex_c\":17.7,\"heatindex_f\":63.9,\"dewpoint_c\":14.2,\"dewpoint_f\":57.6,\"will_it_rain\":1,\"chance_of_rain\":96,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":10.0,\"vis_miles\":6.0,\"gust_mph\":14.5,\"gust_kph\":23.4,\"uv\":4.0},{\"time_epoch\":1657364400,\"time\":\"2022-07-09 14:00\",\"temp_c\":17.6,\"temp_f\":63.7,\"is_day\":1,\"condition\":{\"text\":\"Moderate or heavy rain shower\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/day/356.png\",\"code\":1243},\"wind_mph\":8.5,\"wind_kph\":13.7,\"wind_degree\":352,\"wind_dir\":\"N\",\"pressure_mb\":1015.0,\"pressure_in\":29.96,\"precip_mm\":5.1,\"precip_in\":0.2,\"humidity\":80,\"cloud\":79,\"feelslike_c\":17.6,\"feelslike_f\":63.7,\"windchill_c\":17.6,\"windchill_f\":63.7,\"heatindex_c\":17.6,\"heatindex_f\":63.7,\"dewpoint_c\":14.1,\"dewpoint_f\":57.4,\"will_it_rain\":1,\"chance_of_rain\":96,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":7.0,\"vis_miles\":4.0,\"gust_mph\":15.7,\"gust_kph\":25.2,\"uv\":4.0},{\"time_epoch\":1657368000,\"time\":\"2022-07-09 15:00\",\"temp_c\":17.4,\"temp_f\":63.3,\"is_day\":1,\"condition\":{\"text\":\"Light rain shower\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/day/353.png\",\"code\":1240},\"wind_mph\":7.8,\"wind_kph\":12.6,\"wind_degree\":354,\"wind_dir\":\"N\",\"pressure_mb\":1014.0,\"pressure_in\":29.96,\"precip_mm\":0.7,\"precip_in\":0.03,\"humidity\":81,\"cloud\":90,\"feelslike_c\":17.4,\"feelslike_f\":63.3,\"windchill_c\":17.4,\"windchill_f\":63.3,\"heatindex_c\":17.4,\"heatindex_f\":63.3,\"dewpoint_c\":14.2,\"dewpoint_f\":57.6,\"will_it_rain\":1,\"chance_of_rain\":96,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":10.0,\"vis_miles\":6.0,\"gust_mph\":9.8,\"gust_kph\":15.8,\"uv\":4.0},{\"time_epoch\":1657371600,\"time\":\"2022-07-09 16:00\",\"temp_c\":17.2,\"temp_f\":63.0,\"is_day\":1,\"condition\":{\"text\":\"Light rain shower\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/day/353.png\",\"code\":1240},\"wind_mph\":6.5,\"wind_kph\":10.4,\"wind_degree\":358,\"wind_dir\":\"N\",\"pressure_mb\":1015.0,\"pressure_in\":29.96,\"precip_mm\":0.4,\"precip_in\":0.02,\"humidity\":82,\"cloud\":94,\"feelslike_c\":17.2,\"feelslike_f\":63.0,\"windchill_c\":17.2,\"windchill_f\":63.0,\"heatindex_c\":17.2,\"heatindex_f\":63.0,\"dewpoint_c\":14.2,\"dewpoint_f\":57.6,\"will_it_rain\":1,\"chance_of_rain\":92,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":10.0,\"vis_miles\":6.0,\"gust_mph\":8.3,\"gust_kph\":13.3,\"uv\":4.0},{\"time_epoch\":1657375200,\"time\":\"2022-07-09 17:00\",\"temp_c\":17.1,\"temp_f\":62.8,\"is_day\":1,\"condition\":{\"text\":\"Light rain shower\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/day/353.png\",\"code\":1240},\"wind_mph\":5.1,\"wind_kph\":8.3,\"wind_degree\":3,\"wind_dir\":\"N\",\"pressure_mb\":1015.0,\"pressure_in\":29.96,\"precip_mm\":1.9,\"precip_in\":0.07,\"humidity\":83,\"cloud\":86,\"feelslike_c\":17.1,\"feelslike_f\":62.8,\"windchill_c\":17.1,\"windchill_f\":62.8,\"heatindex_c\":17.1,\"heatindex_f\":62.8,\"dewpoint_c\":14.2,\"dewpoint_f\":57.6,\"will_it_rain\":1,\"chance_of_rain\":94,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":10.0,\"vis_miles\":6.0,\"gust_mph\":7.4,\"gust_kph\":11.9,\"uv\":4.0},{\"time_epoch\":1657378800,\"time\":\"2022-07-09 18:00\",\"temp_c\":17.1,\"temp_f\":62.8,\"is_day\":1,\"condition\":{\"text\":\"Patchy rain possible\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/day/176.png\",\"code\":1063},\"wind_mph\":4.0,\"wind_kph\":6.5,\"wind_degree\":1,\"wind_dir\":\"N\",\"pressure_mb\":1015.0,\"pressure_in\":29.96,\"precip_mm\":0.1,\"precip_in\":0.0,\"humidity\":84,\"cloud\":71,\"feelslike_c\":17.1,\"feelslike_f\":62.8,\"windchill_c\":17.1,\"windchill_f\":62.8,\"heatindex_c\":17.1,\"heatindex_f\":62.8,\"dewpoint_c\":14.3,\"dewpoint_f\":57.7,\"will_it_rain\":0,\"chance_of_rain\":60,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":10.0,\"vis_miles\":6.0,\"gust_mph\":4.7,\"gust_kph\":7.6,\"uv\":4.0},{\"time_epoch\":1657382400,\"time\":\"2022-07-09 19:00\",\"temp_c\":15.0,\"temp_f\":59.0,\"is_day\":1,\"condition\":{\"text\":\"Light rain shower\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/day/353.png\",\"code\":1240},\"wind_mph\":2.9,\"wind_kph\":4.7,\"wind_degree\":337,\"wind_dir\":\"NNW\",\"pressure_mb\":1015.0,\"pressure_in\":29.97,\"precip_mm\":0.5,\"precip_in\":0.02,\"humidity\":85,\"cloud\":50,\"feelslike_c\":15.5,\"feelslike_f\":59.9,\"windchill_c\":15.5,\"windchill_f\":59.9,\"heatindex_c\":15.0,\"heatindex_f\":59.0,\"dewpoint_c\":12.6,\"dewpoint_f\":54.7,\"will_it_rain\":0,\"chance_of_rain\":61,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":10.0,\"vis_miles\":6.0,\"gust_mph\":4.5,\"gust_kph\":7.2,\"uv\":4.0},{\"time_epoch\":1657386000,\"time\":\"2022-07-09 20:00\",\"temp_c\":12.5,\"temp_f\":54.5,\"is_day\":1,\"condition\":{\"text\":\"Partly cloudy\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/day/116.png\",\"code\":1003},\"wind_mph\":3.1,\"wind_kph\":5.0,\"wind_degree\":306,\"wind_dir\":\"NW\",\"pressure_mb\":1015.0,\"pressure_in\":29.98,\"precip_mm\":0.0,\"precip_in\":0.0,\"humidity\":91,\"cloud\":31,\"feelslike_c\":12.6,\"feelslike_f\":54.7,\"windchill_c\":12.6,\"windchill_f\":54.7,\"heatindex_c\":12.5,\"heatindex_f\":54.5,\"dewpoint_c\":11.1,\"dewpoint_f\":52.0,\"will_it_rain\":0,\"chance_of_rain\":0,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":10.0,\"vis_miles\":6.0,\"gust_mph\":6.5,\"gust_kph\":10.4,\"uv\":4.0},{\"time_epoch\":1657389600,\"time\":\"2022-07-09 21:00\",\"temp_c\":11.3,\"temp_f\":52.3,\"is_day\":1,\"condition\":{\"text\":\"Mist\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/day/143.png\",\"code\":1030},\"wind_mph\":3.8,\"wind_kph\":6.1,\"wind_degree\":286,\"wind_dir\":\"WNW\",\"pressure_mb\":1016.0,\"pressure_in\":30.0,\"precip_mm\":0.0,\"precip_in\":0.0,\"humidity\":94,\"cloud\":33,\"feelslike_c\":10.9,\"feelslike_f\":51.6,\"windchill_c\":10.9,\"windchill_f\":51.6,\"heatindex_c\":11.3,\"heatindex_f\":52.3,\"dewpoint_c\":10.4,\"dewpoint_f\":50.7,\"will_it_rain\":0,\"chance_of_rain\":0,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":2.0,\"vis_miles\":1.0,\"gust_mph\":8.1,\"gust_kph\":13.0,\"uv\":3.0},{\"time_epoch\":1657393200,\"time\":\"2022-07-09 22:00\",\"temp_c\":10.7,\"temp_f\":51.3,\"is_day\":0,\"condition\":{\"text\":\"Mist\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/night/143.png\",\"code\":1030},\"wind_mph\":3.8,\"wind_kph\":6.1,\"wind_degree\":272,\"wind_dir\":\"W\",\"pressure_mb\":1016.0,\"pressure_in\":30.01,\"precip_mm\":0.0,\"precip_in\":0.0,\"humidity\":96,\"cloud\":46,\"feelslike_c\":10.2,\"feelslike_f\":50.4,\"windchill_c\":10.2,\"windchill_f\":50.4,\"heatindex_c\":10.7,\"heatindex_f\":51.3,\"dewpoint_c\":10.0,\"dewpoint_f\":50.0,\"will_it_rain\":0,\"chance_of_rain\":0,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":2.0,\"vis_miles\":1.0,\"gust_mph\":8.1,\"gust_kph\":13.0,\"uv\":1.0},{\"time_epoch\":1657396800,\"time\":\"2022-07-09 23:00\",\"temp_c\":10.2,\"temp_f\":50.4,\"is_day\":0,\"condition\":{\"text\":\"Mist\",\"icon\":\"//cdn.weatherapi.com/weather/64x64/night/143.png\",\"code\":1030},\"wind_mph\":3.8,\"wind_kph\":6.1,\"wind_degree\":270,\"wind_dir\":\"W\",\"pressure_mb\":1016.0,\"pressure_in\":30.01,\"precip_mm\":0.0,\"precip_in\":0.0,\"humidity\":96,\"cloud\":53,\"feelslike_c\":9.7,\"feelslike_f\":49.5,\"windchill_c\":9.7,\"windchill_f\":49.5,\"heatindex_c\":10.2,\"heatindex_f\":50.4,\"dewpoint_c\":9.6,\"dewpoint_f\":49.3,\"will_it_rain\":0,\"chance_of_rain\":0,\"will_it_snow\":0,\"chance_of_snow\":0,\"vis_km\":2.0,\"vis_miles\":1.0,\"gust_mph\":8.1,\"gust_kph\":13.0,\"uv\":1.0}]}]}}"
  "parsing.JsonParsedForecast" should "create the class and write in the table correctly" in {
    val current = JsonParsedForecast(forecastJsonResponse, headerExample)
    val testFile = TEST_FILE_PATH
    current.writeDataInTable(testFile)

    val inputStream = new FileInputStream(testFile)
    val workbook = new XSSFWorkbook(inputStream)
    val sheet = workbook.getSheet(current.command)

    val lastRowNum = sheet.getLastRowNum
    val currentRow = sheet.getRow(lastRowNum)

    val getDateResponse = current.header.getOrElse("date", "unidentified")

    currentRow.getCell(0).getNumericCellValue shouldBe lastRowNum.toDouble
    currentRow.getCell(1).getStringCellValue shouldBe getDateResponse.toString.substring(4)
    currentRow.getCell(2).getStringCellValue shouldBe current.rawValue
    currentRow.getCell(3).getStringCellValue shouldBe current.command

    inputStream.close()
    workbook.close()
  }

  val footballJsonResponse =
    "{\"football\":[{\"stadium\":\"Fc Rapid Bucuresti\",\"country\":\"Romania\",\"region\":\"\",\"tournament\":\"UEFA Nations League\",\"start\":\"2022-06-14 19:45\",\"match\":\"Romania vs Montenegro\"},{\"stadium\":\"Albion Rovers Fc\",\"country\":\"United Kingdom\",\"region\":\"\",\"tournament\":\"Scottish League Cup\",\"start\":\"2022-07-09 15:00\",\"match\":\"Albion Rovers vs Livingston\"},{\"stadium\":\"Ayr United Fc\",\"country\":\"United Kingdom\",\"region\":\"\",\"tournament\":\"Scottish League Cup\",\"start\":\"2022-07-09 15:00\",\"match\":\"Ayr United vs Elgin City\"},{\"stadium\":\"Cowdenbeath Fc\",\"country\":\"United Kingdom\",\"region\":\"\",\"tournament\":\"Scottish League Cup\",\"start\":\"2022-07-09 15:00\",\"match\":\"Cowdenbeath vs Airdrieonians\"},{\"stadium\":\"Dumbarton Fc\",\"country\":\"United Kingdom\",\"region\":\"\",\"tournament\":\"Scottish League Cup\",\"start\":\"2022-07-09 15:00\",\"match\":\"Dumbarton vs Stirling Albion\"},{\"stadium\":\"Dundee\",\"country\":\"United Kingdom\",\"region\":\"\",\"tournament\":\"Scottish League Cup\",\"start\":\"2022-07-09 15:00\",\"match\":\"Dundee vs Hamilton Academical\"},{\"stadium\":\"East Fife Fc\",\"country\":\"United Kingdom\",\"region\":\"\",\"tournament\":\"Scottish League Cup\",\"start\":\"2022-07-09 15:00\",\"match\":\"East Fife vs Dunfermline Athletic\"},{\"stadium\":\"Fraserburgh Fc\",\"country\":\"United Kingdom\",\"region\":\"\",\"tournament\":\"Scottish League Cup\",\"start\":\"2022-07-09 15:00\",\"match\":\"Fraserburgh vs Kilmarnock\"},{\"stadium\":\"Hibernian Fc\",\"country\":\"United Kingdom\",\"region\":\"\",\"tournament\":\"Scottish League Cup\",\"start\":\"2022-07-09 15:00\",\"match\":\"Hibernian vs Clyde\"},{\"stadium\":\"Greenock Morton Fc\",\"country\":\"United Kingdom\",\"region\":\"\",\"tournament\":\"Scottish League Cup\",\"start\":\"2022-07-09 15:00\",\"match\":\"Morton vs Falkirk\"}],\"cricket\":[{\"stadium\":\"Trent Bridge, Nottingham\",\"country\":\"United Kingdom\",\"region\":\"\",\"tournament\":\"England vs India Twenty20 Series 2022\",\"start\":\"2022-07-10 14:30\",\"match\":\"England vs India\"}],\"golf\":[{\"stadium\":\"Firestone Country Club\",\"country\":\"United States of America\",\"region\":\"\",\"tournament\":\"Bridgestone Senior Players Championship Round 3\",\"start\":\"2022-07-09 14:20\",\"match\":\"Stephen Ames v Kevin Sutherland v Tom Pernice Jr.\"},{\"stadium\":\"Firestone Country Club\",\"country\":\"United States of America\",\"region\":\"\",\"tournament\":\"Bridgestone Senior Players Championship Round 3\",\"start\":\"2022-07-09 14:20\",\"match\":\"Lee Janzen v Brandt Jobe v Dicky Pride\"},{\"stadium\":\"Firestone Country Club\",\"country\":\"United States of America\",\"region\":\"\",\"tournament\":\"Bridgestone Senior Players Championship Round 3\",\"start\":\"2022-07-09 14:30\",\"match\":\"Cameron Beckman v Mark Walker v Retief Goosen\"},{\"stadium\":\"Firestone Country Club\",\"country\":\"United States of America\",\"region\":\"\",\"tournament\":\"Bridgestone Senior Players Championship Round 3\",\"start\":\"2022-07-09 14:30\",\"match\":\"John Senden v Brett Quigley v Tom Gillis\"},{\"stadium\":\"Firestone Country Club\",\"country\":\"United States of America\",\"region\":\"\",\"tournament\":\"Bridgestone Senior Players Championship Round 3\",\"start\":\"2022-07-09 14:40\",\"match\":\"David Duval v Colin Montgomerie v Paul Broadhurst\"},{\"stadium\":\"Firestone Country Club\",\"country\":\"United States of America\",\"region\":\"\",\"tournament\":\"Bridgestone Senior Players Championship Round 3\",\"start\":\"2022-07-09 14:40\",\"match\":\"Steve Flesch v Thongchai Jaidee v Bernhard Langer\"},{\"stadium\":\"Firestone Country Club\",\"country\":\"United States of America\",\"region\":\"\",\"tournament\":\"Bridgestone Senior Players Championship Round 3\",\"start\":\"2022-07-09 14:50\",\"match\":\"Tom Byrum v Vijay Singh v Scott Parel\"},{\"stadium\":\"Firestone Country Club\",\"country\":\"United States of America\",\"region\":\"\",\"tournament\":\"Bridgestone Senior Players Championship Round 3\",\"start\":\"2022-07-09 14:50\",\"match\":\"Gene Sauers v Duffy Waldorf v Joey Sindelar\"},{\"stadium\":\"Firestone Country Club\",\"country\":\"United States of America\",\"region\":\"\",\"tournament\":\"Bridgestone Senior Players Championship Round 3\",\"start\":\"2022-07-09 15:00\",\"match\":\"John Huston v Y.E. Yang v Wes Short, Jr.\"},{\"stadium\":\"Firestone Country Club\",\"country\":\"United States of America\",\"region\":\"\",\"tournament\":\"Bridgestone Senior Players Championship Round 3\",\"start\":\"2022-07-09 15:00\",\"match\":\"Jay Haas v Glen Day v Ken Tanigawa\"}]}"
  "parsing.JsonParsedFootball" should "create the class and write in the table correctly" in {
    val current = JsonParsedFootball(footballJsonResponse, headerExample)
    val testFile = TEST_FILE_PATH
    current.writeDataInTable(testFile)

    val inputStream = new FileInputStream(testFile)
    val workbook = new XSSFWorkbook(inputStream)
    val sheet = workbook.getSheet(current.command)

    val lastRowNum = sheet.getLastRowNum
    val currentRow = sheet.getRow(lastRowNum)

    val getDateResponse = current.header.getOrElse("date", "unidentified")

    currentRow.getCell(0).getNumericCellValue shouldBe lastRowNum.toDouble
    currentRow.getCell(1).getStringCellValue shouldBe getDateResponse.toString.substring(4)
    currentRow.getCell(2).getStringCellValue shouldBe current.rawValue
    currentRow.getCell(3).getStringCellValue shouldBe current.command

    inputStream.close()
    workbook.close()
  }

  val astronomyJsonResponse =
    "{\"location\":{\"name\":\"Sofia\",\"region\":\"Grad Sofiya\",\"country\":\"Bulgaria\",\"lat\":42.68,\"lon\":23.32,\"tz_id\":\"Europe/Sofia\",\"localtime_epoch\":1657363160,\"localtime\":\"2022-07-09 13:39\"},\"astronomy\":{\"astro\":{\"sunrise\":\"05:50 AM\",\"sunset\":\"09:02 PM\",\"moonrise\":\"11:49 AM\",\"moonset\":\"01:38 AM\",\"moon_phase\":\"Waxing Crescent\",\"moon_illumination\":\"46\"}}}"
  "parsing.JsonParsedAstronomy" should "create the class and write in the table correctly" in {
    val current = JsonParsedAstronomy(astronomyJsonResponse, headerExample)
    val testFile = TEST_FILE_PATH
    current.writeDataInTable(testFile)

    val inputStream = new FileInputStream(testFile)
    val workbook = new XSSFWorkbook(inputStream)
    val sheet = workbook.getSheet(current.command)

    val lastRowNum = sheet.getLastRowNum
    val currentRow = sheet.getRow(lastRowNum)

    val getDateResponse = current.header.getOrElse("date", "unidentified")

    currentRow.getCell(0).getNumericCellValue shouldBe lastRowNum.toDouble
    currentRow.getCell(1).getStringCellValue shouldBe getDateResponse.toString.substring(4)
    currentRow.getCell(2).getStringCellValue shouldBe current.rawValue
    currentRow.getCell(3).getStringCellValue shouldBe current.command

    inputStream.close()
    workbook.close()
  }

  val timezoneJsonResponse =
    "{\"location\":{\"name\":\"Sofia\",\"region\":\"Grad Sofiya\",\"country\":\"Bulgaria\",\"lat\":42.68,\"lon\":23.32,\"tz_id\":\"Europe/Sofia\",\"localtime_epoch\":1657363170,\"localtime\":\"2022-07-09 13:39\"}}"
  "parsing.JsonParsedTimeZone" should "create the class and write in the table correctly" in {
    val current = JsonParsedTimeZone(timezoneJsonResponse, headerExample)
    val testFile = TEST_FILE_PATH
    current.writeDataInTable(testFile)

    val inputStream = new FileInputStream(testFile)
    val workbook = new XSSFWorkbook(inputStream)
    val sheet = workbook.getSheet(current.command)

    val lastRowNum = sheet.getLastRowNum
    val currentRow = sheet.getRow(lastRowNum)

    val getDateResponse = current.header.getOrElse("date", "unidentified")

    currentRow.getCell(0).getNumericCellValue shouldBe lastRowNum.toDouble
    currentRow.getCell(1).getStringCellValue shouldBe getDateResponse.toString.substring(4)
    currentRow.getCell(2).getStringCellValue shouldBe current.rawValue
    currentRow.getCell(3).getStringCellValue shouldBe current.command

    inputStream.close()
    workbook.close()
  }

  

  
