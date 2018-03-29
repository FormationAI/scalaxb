import scalaxb.compiler.wsdl11.{Driver}
import java.io.{File}
import scalaxb.compiler.{Config, Effect}
import scalaxb.compiler.ConfigEntry._

object Wsdl11Soap11CatsEffectTest extends TestBase {
  override val module = new Driver // with Verbose

  val packageName = "genericbarcode"
  val inFile  = new File("integration/src/test/resources/genericbarcode.wsdl")
  val config =  Config.default.update(PackageNames(Map(None -> Some(packageName)))).
      update(Outdir(tmp)).
      update(GenerateHttp4sClient).
      remove(GenerateDispatchClient).
      update(GeneratePackageDir).
      update(ClientEffect(Effect.CatsEffect))

  println("CONFIG === " + config)

  lazy val generated = module.process(inFile, config)
  "stockquote.scala file must compile with http4s" in {
    (List("""
      import fs2.Stream
      import genericbarcode._
      import cats.effect.IO
      import org.http4s.client.blaze._
      import scalaxb._
      """,
      """
      val response = Http1Client.stream[IO]().evalMap { client =>
        val service = BarCodeSoap[IO](Soap11Client(Http4sClient(client)))
        val data = BarCodeData(120, 120, 0, 1, 1, 20, 20, true, None, None, None, 10.0f, Both, CodeEAN128B, NoneType, BottomCenter, PNG)
        println(scalaxb.toXML(data, "BarCodeParam", defaultScope))
        service.generateBarCode(data, Some("1234"))
      }.compile.last.unsafeRunSync.get
      println(response)
      """,
      """response.toString.contains("iVB")"""), generated) must evaluateTo(true,
      outdir = "./tmp", usecurrentcp = true)
  }  
}
