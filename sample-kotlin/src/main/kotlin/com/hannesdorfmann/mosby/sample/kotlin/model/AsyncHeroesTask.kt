package com.hannesdorfmann.mosby.sample.kotlin.model

import android.os.AsyncTask
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import java.util.Collections

/**
 * An AsyncTask that loads a list of heroes
 *
 * @author Hannes Dorfmann
 */
@SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", "I know what I'm doing")
public class AsyncHeroesTask(val pullToRefresh: Boolean,
                             val successful: (List<Hero>, Boolean) -> Unit,
                             val error: (Exception, Boolean) -> Unit) : AsyncTask<Void, Void, List<Hero>>() {

    companion object Counter {
        var requestCounter: Int = 0
    }


    override fun doInBackground(vararg params: Void?): List<Hero>? {

        Thread.sleep(2000)

        requestCounter++

        if (requestCounter % 2 != 0) return null;


        var heroes = arrayListOf(
                Hero("Batwoman", "https://upload.wikimedia.org/wikipedia/en/2/24/Batwoman.png"),
                Hero("Catwoman", "https://upload.wikimedia.org/wikipedia/en/7/71/Adam_Hughe%27s_Catwoman.jpg"),
                Hero("Devi", "https://upload.wikimedia.org/wikipedia/en/2/2f/Devi_no_1.png"),
                Hero("Domino", "https://upload.wikimedia.org/wikipedia/en/5/5a/DominoX-Men.jpg"),
                Hero("Jade", "https://upload.wikimedia.org/wikipedia/en/3/35/Green_Lantern_Jade.jpg"),
                Hero("Magma", "https://upload.wikimedia.org/wikipedia/en/b/ba/MarvelComicsMagma.jpg"),
                Hero("Pixie", "https://upload.wikimedia.org/wikipedia/en/d/d6/PixieWiki.png"),
                Hero("Rad", "https://upload.wikimedia.org/wikipedia/en/8/8c/Rad.jpg"),
                Hero("Jessica Jones", "http://oyster.ignimgs.com/wordpress/stg.ign.com/2014/09/25c-300x240.jpg"),
                Hero("Blade", "http://oyster.ignimgs.com/wordpress/stg.ign.com/2014/09/24d-300x426.jpg"),
                Hero("Ghost Rider", "http://oyster.ignimgs.com/wordpress/stg.ign.com/2014/09/23b-300x187.jpg"),
                Hero("Captain Marvel", "http://oyster.ignimgs.com/wordpress/stg.ign.com/2014/09/22b-300x454.jpg"),
                Hero("Luke Cage", "http://oyster.ignimgs.com/wordpress/stg.ign.com/2014/09/21a-300x455.jpg"),
                Hero("Spider Woman", "http://oyster.ignimgs.com/wordpress/stg.ign.com/2014/09/20a-300x450.jpg"),
                Hero("Silver Surfer", "http://oyster.ignimgs.com/wordpress/stg.ign.com/2014/09/19b-300x455.jpg"),
                Hero("Beast", "http://oyster.ignimgs.com/wordpress/stg.ign.com/2014/09/18a-300x308.jpg"),
                Hero("Thing", "http://oyster.ignimgs.com/wordpress/stg.ign.com/2014/09/17b-300x473.jpg"),
                Hero("Kitty Pryde", "http://oyster.ignimgs.com/wordpress/stg.ign.com/2014/09/16a-300x448.jpg"),
                Hero("Doctor Strange", "http://oyster.ignimgs.com/wordpress/stg.ign.com/2014/09/15c-300x467.jpg"),
                Hero("Black Panter", "http://oyster.ignimgs.com/wordpress/stg.ign.com/2014/09/14b-300x444.jpg"),
                Hero("The Invisible Woman", "http://oyster.ignimgs.com/wordpress/stg.ign.com/2014/09/13a-300x232.jpg"),
                Hero("Nick Fury", "http://oyster.ignimgs.com/wordpress/stg.ign.com/2014/09/12a-300x442.jpg"),
                Hero("Storm", "http://oyster.ignimgs.com/wordpress/stg.ign.com/2014/09/11b-300x168.jpg"),
                Hero("Iron Man", "http://oyster.ignimgs.com/wordpress/stg.ign.com/2014/09/10e-300x450.jpg"),
                Hero("Professor X", "http://oyster.ignimgs.com/wordpress/stg.ign.com/2014/09/9c-300x397.jpg"),
                Hero("Hulk", "http://oyster.ignimgs.com/wordpress/stg.ign.com/2014/09/8d-300x230.jpg"),
                Hero("Cyclops", "http://oyster.ignimgs.com/wordpress/stg.ign.com/2014/09/7c-300x447.jpg"),
                Hero("Thor", "http://oyster.ignimgs.com/wordpress/stg.ign.com/2014/09/6d-300x455.jpg"),
                Hero("Jean Grey", "http://oyster.ignimgs.com/wordpress/stg.ign.com/2014/09/5b-300x466.png"),
                Hero("Wolverine", "http://oyster.ignimgs.com/wordpress/stg.ign.com/2014/09/4e-300x455.jpg"),
                Hero("Daredevil", "http://oyster.ignimgs.com/wordpress/stg.ign.com/2014/09/3e-300x455.jpg"),
                Hero("Captain America", "http://oyster.ignimgs.com/wordpress/stg.ign.com/2014/09/2e-300x413.jpg"),
                Hero("Spider-Man", "http://oyster.ignimgs.com/wordpress/stg.ign.com/2014/09/1j-720x1091.jpg"),
                Hero("Batman", "http://static.comicvine.com/uploads/scale_small/6/66303/2961034-screen+shot+2013-04-03+at+5.56.06+am.png")
        )

        Collections.shuffle(heroes)

        return heroes;
    }

    override fun onPostExecute(heroes: List<Hero>?) {
        when (heroes) {
            null -> error(Exception(), pullToRefresh)
            else -> successful(heroes, pullToRefresh)
        }
    }
}