package com.hannesdorfmann.mosby.sample.flow.model

import rx.Observable
import java.util.*

/**
 *
 *
 * @author Hannes Dorfmann
 */
class Atlas {

  private var requestCounter = 0
  private val errorAfter = 3
  private val SLEEP = 2000L;

  private val countriesMap: Map<Int, Country> = mapOf(
      1 to Country(1, "Italy", "Rome", 60795612, 301338, "Euro",
          "http://www.theflagshop.co.uk/ekmps/shops/speed/images/italian-italy-flag-130-p.jpg"),

      2 to Country(2, "Germany", "Berlin", 81459000, 357168, "Euro",
          "http://www.german-flag.org/german-640.gif"),

      3 to Country(3, "France", "Paris", 66627602, 643801, "Euro",
          "http://www.nationalflaggen.de/media/flags/flagge-frankreich.gif"),

      4 to Country(4, "United Kingdom", "London", 64716000, 242495, "Pound sterling",
          "http://resources.woodlands-junior.kent.sch.uk/customs/images/uk.jpgf"),

      5 to Country(5, "Spain", "Madrid", 46439864, 505990, "Euro",
          "http://www.flag-works.com/nationpictures/Spain%20w%20seal.JPG"),

      6 to Country(6, "Sweden", "Stockholm", 9851017, 450295, "Euro",
          "http://ecx.images-amazon.com/images/I/51hwksFrPiL._SL1300_.jpg"),


      7 to Country(7, "Finnland", "Helsinki", 5486125, 338424, "Euro",
          "http://ecx.images-amazon.com/images/I/21ExYq%2BVfOL.jpg"),


      8 to Country(8, "Norway", "Oslo", 5214900, 385178, "Norwegian krone",
          "http://www.flaggen-server.de/europa2/norwegeng.png"),

      9 to Country(9, "Russia", "Moscow", 144192450, 17098242, "Russian ruble",
          "http://www.eu-asien.de/assets/images/Russland/Flagge-Russland-marked.jpg"),

      10 to Country(10, "China", "Beijing", 1376049000, 9596961, "Renminbi",
          "http://www.nationalflaggen.de/media/flags/flagge-china.gif"),


      11 to Country(11, "India", "New Delhi", 1276267000, 3287590, "Indian rupee",
          "http://www.nationalflaggen.de/media/flags/flagge-indien.gif"),

      12 to Country(12, "Japan", "Tokyo", 126919659, 377944, "Yen",
          "http://www.nationalflaggen.de/media/flags/flagge-japan.gif"),

      13 to Country(13, "Egypt", "Cairo", 90468000, 1010407, "Egyptian pound",
          "http://www.nationalflaggen.de/media/flags/flagge-aegypten.gif"),

      14 to Country(14, "South Africa", "Petropia", 54956900, 1221037, "South African rand",
          "http://www.kapstadt-tour.de/allgemeines/images/flaggen/1994_.gif"),

      15 to Country(15, "United Arab Emirates", "Abu Dhabi", 5779760, 83600, "UAE dirham",
          "http://www.nationalflaggen.de/media/flags/flagge-vereinigte-arabische-emirate.gif"),

      16 to Country(16, "Australia", "Canberra", 24007900, 7692024, "Australian dollar",
          "http://www.nationalflaggen.de/media/flags/flagge-vereinigte-arabische-emirate.gif"),

      16 to Country(16, "Australia", "Canberra", 24007900, 7692024, "Australian dollar",
          "http://ecx.images-amazon.com/images/I/61BERUrFSbL._SL1500_.jpg"),

      17 to Country(17, "Brazil", "Brasilia", 205338000, 8515767, "Real",
          "http://shadowhelix.pegasus.de/images/thumb/6/61/Flagge_Brasilien.svg/720px-Flagge_Brasilien.svg.png"),

      18 to Country(18, "Argentina", "Buenos Aires", 43417000, 2780400, "Peso",
          "http://www.flaggen-server.de/amerika2/argentinieng.gif"),


      19 to Country(18, "Mexico", "Mexico City", 119530753, 1972550, "Peso",
          "http://www.flaggen-server.de/amerika2/mexikog.gif"),


      20 to Country(20, "United States of America", "Washington, D.C.", 322369319, 9857306,
          "United States dollar",
          "http://www.nationalflaggen.de/media/flags/flagge-vereinigte-staaten-von-amerika-usa.gif"),


      21 to Country(21, "Canada", "Ottawa", 35985751, 9984670, "Canadian dollar",
          "https://pixabay.com/static/uploads/photo/2012/04/10/23/27/canada-27003_960_720.png")


  )

  private val themeImageMap: Map<Int, String> = mapOf(
      1 to "http://www.feratel.com/fileadmin/_migrated/pics/Italien_1.jpg",
      2 to "http://www.feriendomizile-in-deutschland.de/wp-content/uploads/2010/12/Deutschland_Berlin1.jpg",
      3 to "http://unterkunft-reise.com/wp-content/uploads/2012/07/Paris-Eiffel-Seine.jpg",
      4 to "http://www.london.citysam.de/fotos-london-p/london-sehenswuerdigkeiten-7.jpg",
      5 to "http://losnavarros.de/wp-content/uploads/2015/05/Plaza-Espa%C3%B1a-de-Sevilla.jpg",
      6 to "http://osz-mals-informatik.com/4fowi03/images/verzeichnis1/schweden-country-page-2.jpg",
      7 to "http://www.urlaubplanen.org/fotos/europa/finnland/helsinki-finnland.jpg",
      8 to "http://www.rundreise-norwegen.de/wp-content/uploads/2013/01/Trolltunga.jpg",
      9 to "http://www.stadtplanlupe.de/wp-content/uploads/2009/09/Fotolia_5216811_XS.jpg",
      10 to "https://www.skr.de/blog/wp-content/uploads/2014/04/Verbotene-Stadt.jpg",
      11 to "http://www.sonnenlaender.de/img/indien/sehenswuerdigkeiten/taj-mahal.jpg",
      12 to "http://frendy.de/media/b26/d20/72a/fujiyama.thumb_2x2.jpg",
      13 to "http://frendy.de/media/913/270/330/pyramiden-von-gizeh-kairo-aegypten.thumb_2x2.jpg",
      14 to "http://www.urlaubplanen.org/fotos/afrika/suedafrika/sehenswuerdigkeiten/kapstadt.jpg",
      15 to "http://img.welt.de/img/staedtereisen/crop127827394/5956936921-ci3x2l-w900/Vereinigte-Arabische-Emirate-Dubai.jpg",
      16 to "http://australien.sehenswuerdigkeiten.co/files/2012/03/ayers-rock1.jpg",
      17 to "http://www.fliegen-sparen.de/de-wAssets/img/suedamerika/brasilien_ausblick_rio_600.jpg",
      18 to "http://www.argentinienaktuell.com/argentinien-bilder/221_3_mount-fitzroy.jpg",
      19 to "http://www.blogcdn.com/de.engadget.com/media/2012/08/google-street-view-chichen-itza-1345210908.jpg",
      20 to "https://www.flugladen.de/Cms_Data/Contents/BudgetAirDEDE/Media/Slider/United%20States/slider_flights_usa.jpg",
      21 to "http://canada-travel-service.de/cms/sf/keyVisuals/Zentralkanada--East-Side-Hits-and-West-Side-Stories.jpg"
  )

  fun getCountries(
      filter: (Country) -> Boolean = { true }): Observable<List<Country>> = Observable.fromCallable {

    Thread.sleep(SLEEP)

    if (requestCounter++ % errorAfter == 0) {
      throw RuntimeException("Mocked Exception")
    }

    ArrayList(countriesMap.values).filter(filter).sortedBy { it.name }
  }

}