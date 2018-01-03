/*
 * Copyright 2015 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hannesdorfmann.mosby3.sample.mail.model.mail;

import com.hannesdorfmann.mosby3.sample.mail.model.contact.Person;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * Just a simple class that generates Mails
 *
 * @author Hannes Dorfmann
 */
public class RandomMailGenerator implements MailGenerator {

  public Random random = new Random();

  private String[] hiTed = new String[] {
      "Hi Ted! ", "Yo Ted! ", "Hi Bro! ", "Hey Bro!", "Dude! ", "Dear Ted! ",
      "Ted Ted Teddy Bear! ", "Yo Teddy Bear!", "What's up? ", "Hey, what's going on?",
      "Hello Ted. "
  };

  private String[] subjects = new String[] {
      "Tonight at MacLaren’s", "Beer at MacLaren’s", "Laser tag", "Challenge accepted", "Yo Dude",
      "Hey Bro", "What's up?", "Party tonight", "Hanging out", "Wear Suits!"
  };

  private String[] barneyQuotes = new String[] {
      "Think of me like Yoda, but instead of being little and\n"
          + "green I wear suits and I’m awesome. I’m your bro—I’m Broda!",
      "Okay, pep talk! You can do this, but to be more\n"
          + "accurate, you probably can’t. You’re way out of practice and she’s way too hot\n"
          + "for you. So, remember, it’s not about scoring. It’s about believing you can do\n"
          + "it, even though you probably can’t. Go get ‘em, tiger!",
      "“It’s gonna be legend-... wait for it… and I hope\n"
          + "you’re not lactose intolerant because the second half of that word is DAIRY!”",
      "Jesus waited threedays to come back to life. It was perfect! If he had only waited one day, a lot of people wouldn’t have even heard he\n"
          + "died. They’d be all, “Hey Jesus, what up?” and Jesus would probably\n"
          + "be like, “What up? I died yesterday!” \n"
          + "and they’d be all, “Uh, you look pretty alive to me, dude…” and\n"
          + "then Jesus would have to explain how he was resurrected, and how it was a\n"
          + "miracle, and the dude’d be like “Uhh okay, whatever you say, bro…” \n"
          + "And he’s not gonna come back on a Saturday. Everybody’s busy, doing chores, workin’ the loom, trimmin’ the beard,\n"
          + "NO. He waited the perfect number of days, three. Plus it’s Sunday, so\n"
          + "everyone’s in church already, and they’re all in there like “Oh no, Jesus\n"
          + "is dead”, and then BAM! He bursts in the back door, runnin’ up the aisle,\n"
          + "everyone’s totally psyched, and FYI, that’s when he invented the high five.\n"
          + "That’s why we wait three days to call a woman, because that’s how long Jesus\n"
          + "wants us to wait…. True story.",
      "In my body, where the shame gland should be, there is\n"
          + "a second awesome gland. True story.",
      "Oh right, because there can be too many of something\n"
          + "wonderful. Hey Babe Ruth, easy big fella, let’s not hit too many homers. Hey\n"
          + "Steve Gutenberg, maybe just make three Police Academy movies. America’s laughed\n"
          + "enough.", "Do you have some puritanical hang up on prostitution?\n"
      + "Dude, it’s the world’s oldest profession.",
      "Suits are full of joy. They’re the sartorial\n" + "equivalent of a baby’s smile.",
      "Here’s the mini-cherry on top of the regular cherry on"
          + "top of the sundae of awesomeness that is my life.",
      "A lie is just a great story that someone ruined with the truth.",
      "It's going to be legen...wait for it...and I hope you're not lactose-intolerant cause the second half of that word is...dairy!",
      "Article 2: \"A Bro is always entitled to do something stupid, as long as the rest of his Bros are all doing it.",
      "Believe it or not, I was not always as awesome as I am today",
      "You know what Marshall needs to do. He needs to stop being sad. When I get sad, I stop being sad, and be awesome instead. True story.",
      "I believe you and I met for a reason. It's like the universe was saying, \"Hey Barney, there's this dude, he's pretty cool, but it is your job to make him awesome",
      "Article 100: \"When pulling up to a stoplight, a Bro lowers his window so that all might enjoy his music selection.\"\n"
          + "Corollary: \"If there happens to be a hot chick driving the car next to the Bro, the Bro shall put his sunglasses down to get a better look. If he's not wearing his sunglasses, he will first put them on, then pull down to get a better look.",
      "ARTICLE 54 A Bro is required to go out with his Bros on St. Patty’s Day and other official Bro holidays, including Halloween, New Year’s Eve, and Desperation Day (February 13).",
      "SCIENCE! There is an 83% correlation between the times men wear boutonnieres and the times they get laid. Think about it, proms, weddings, grandmas funerals...Thanks for the redhead Nana. The everyday boutonniere, by Stinson. ",
      "ARTICLE 85 If a Bro buys a new car, he is required to pop the hood when showing it off to his Bros. COROLLARY: His Bros are required to whistle, even if they have no idea what they’re whistling at.",
      "ARTICLE 2 A Bro is always entitled to do something stupid, as long as the rest of his Bros are all doing it.",
      "For some it was the ashes of my parents. For others it was the trophy from Wimbledon and believe it or not, for one busty dullard, it was both. Game, enormous set and match!",
      "ARTICLE 41 A Bro never cries.   EXCEPTIONS: Watching Field of Dreams, E.T., or a sports legend retire",
      "ARTICLE 120 A Bro always calls another Bro by his last name.   EXCEPTION: If a Bro’s last name is also a racial epithet.",
      "ARTICLE 130 If a Bro learns another Bro has been in a traffic accident, he must first ask what type of car he collided with and whether it got totaled before asking if his Bro is okay."
  };

  private String[] marshallQuotes = new String[] {
      "Screw tomorrow, lets go big tonight",
      "Look at this thing! I'll never have cold pizza again! I'll never have cold pizza again...",
      "Hey I have given up peeing in the shower for you!",
      "You're killing me Lily! You're killing me! You have to let me dance my own battles!",
      "That's weird, you still have an answering machine.",
      "My goal is to eventually say things that are so sassy and wise, that there is no possible response other than Mm or Mmhm. And if this is a place where we can share our dreams, I like to think that it is, I hope someday, to earn a...testify!",
      "Oh oh honey sweetie baby. No thirty-two year old woman is happy taking things slow. Trust me, Victoria has got friends from high school posting pictures of second babies on Facebook and you think girlfriend's all like, oh lets just bone a bunch so I'm another year older and still single? Bitch please!",
      "You smell that? That's the smell of urine that isn't Marvin's.",
      "Does anyone have six 9-volt batteries?", "Lily lika lika, the honka honka.",
      "All hail Beercules!",
      "Come on lay some of that classic Scherbatsky mean son of a bitch on me. Treat me like I'm a girl scout trying to sell you cookies.",
      "I don't want to be choosing between two girls. I want to be a complete head over heels idiot for one!",
      "This was bound to happen eventually. I've been trying so hard to suppress like my carefree idiot side that it just rebelled and came out swingin'.",
      "Whoa did someone say generous endowment? I'm Marshall Eriksen, but you can call me, Beercules!",
      "There's babies everywhere. It's like a minefield of cuteness. Look at this little bastard. ",
      "Can I borrow an adult diaper?", "A man can do a lot of living in three hours.",
      "I don't know, homegirl is pretty diabolical.",
      "Aw look at you. Had a girlfriend for five minutes and thinking you're playing with the big boy is adorable... Son, I've been in a relathionship since you had a ponytail and you were playing Dave Matthews on your mama's Casio, I'm a good boyfriend in my sleep. I can rock a killer foot rub at one hand and, brew a kick-ass pot of camomile on the other that would make you weap. Hell, I've forgoten more about microwaving fat-free pop-corn and watching Sandra Bullock movies than you'll ever now but, thanks for your concern rook.",
      "Tell him that I might not be able to give him a grandchild? I don't even know how to have that conversation. ",
      "I'm freaking out, is there a chance I won't be able to have kids? I've been hit in the nuts a lot.",
      "Machines are overrated and someone needs to take them down a peg.",
      "How can you be a New Yorker and never have seen Woody Allen?"
  };

  private String[] lilyQuotes = new String[] {
      "Wow. A genuine Scherbatsky sighting out in nature. At this point, that's like seeing Sasquatch.",
      "I don't need objectivity. You're my best friend, I just need your support.",
      "Why say goodbye to the good things?",
      "You get older, you have kids, you stop stealing, it's sad.",
      "Here he is. Just as hot as when his Tiger Beat photo spread gave a young girl the courage to explore the suddenly unfamiliar topography of her changing body...The Karate Kid!",
      "I'm a hick from Brooklyn who's terrified of living more than ten subway stops from where I was born.",
      "you don't start with the I got caught cheating diamond. You give yourself room to grow.",
      "Okay but hurry, or I'm gonna start doing number eleven on my own. Pilates bitch!",
      "Please help me off the couch so I can storm out!",
      "Don't say that whore's name in front of our baby!",
      "Excuse me? I'll have a mojito and you'll have a no seat ho!",
      "Marshall and I have been together for fifteen years and the only debate about Tommy Boy we've ever had, is whether it's awesome or super awesome. That's love bitch!",
      " Anytime a single guy hangs out with a married woman there are rules that must be followed. Rule number one: Don't use the husband's condoms, that's just rude.",
      "Whoa baby you're packing snow balls and you breathe smells like a mermaid's fart.",
      "Oh Ted, here's another fun little trivia game it's called - name that bitch.",
      "Let's take a little walk down random skank lane.",
      "Of course she is upset - take a look at yourself you dumb slut.",
      "Oh Ted, she's totally got you on the hook.", "Ok, let me try to Canada this up for ya, eh?",
      "Awe you're so sweet, but, compare to that woman I'm like three day old garbage."
  };

  private String[] robinQuotes = new String[] {
      "I know I've missed a couple lately but we always said we'd be there for the big moments.",
      "No I'm not okay, because apparently I'm marrying my dad in a few hours.",
      "Yeah let's prove that we believe in marriage by workin together to help Marshall hide something from his wife!",
      "Aren't you tired of waiting for destiny Ted?",
      "If I stole a scalpel and I cut you open, all I would find is this scared trembling pile of crap. ",
      "Oh we're bustin' apple bags? I can bust apple bags.",
      "Robin Sparkles was a cabin-hold name!", "Don't ever, say that to any girl, ever!",
      "Sometimes in life you have to be assertive and stand up for yourself.",
      "Well proposing a three way was bad, starting without us was worse, finishing in the hallway was the nail in the coffin.",
      "If? Ted those kids jumped into a painting and chased a cartoon fox around for fifteen minutes. Spoon full of sugar? Grow up.",
      "I'm so sorry, you must think I'm totally disgusting. ",
      "I think the smaller turkey just tried to crawl further inside the bigger turkey.",
      "Look, I hate most babies, but your baby; I'm going to love that kid so much. I'm going to pick it up and everything.",
      "I'm going to kill you. I'm going to fly to Chicago, kill you, put your stupid face on a deep dish pizza and eat it. And then maybe catch a Bears game. But mostly the killing and eating your face thing.",
      "Hey here's some breaking news ... there's a zit breaking out on your forehead!",
      "On the plus side, he probably killed some roaches on impact."
  };

  private String[] tedQuotes = new String[] {
      "Just be cool, Lady. Damn.", "It's been a major pleasure, Major Pleasure.",
      "Here's the secret kids. None of us can vow to be perfect. In the end all we can do is promise to love each other with everything we've got. Because love's the best thing we do.",
      "It doesn't have to make sense to make sense.",
      "I refuse to be a part of a third runaway bride situation.",
      "You will be shocked kids, when you discover how easy it is in life to part ways with people forever. That's why, when you find someone you want to keep around, you do something about it.",
      "And that's how it goes kids. The friends, neighbors, drinking buddies and partners in crime you love so much when you're young, as the years go by, you just lose touch.",
      "Singles tables are cruel. I mean what if you went to a wedding and there was a table of all fat guys?",
      "What followed was a long day and a half for Lily, a really long day and a half. It's kind of insane how much happened in just a day and a half.",
      "You see the irony here. The only person who could possibly get Barney back on his feet is Barney.",
      "You know what's weird Stella? Not seeing Star Wars until you're 30.",
      "I love Barney, but I'm not going to jail for him.",
      "It would be nice, just once, not to have to go stag to Coin-Con.",
      "When you believe in people, people come through.",
      "ight years ago I made an ass of myself chasing after you and I made an ass of myself chasing after you a bunch of times since then. I have no regrets because it led me to something I wouldn't trade for the world, it led to you being my friend. So as your friend and a leading expert in the field of making an ass of yourself. I say to you, from the heart, get the hell out of this car.",
      "I need to grow up. Oh by the way I'm breaking a jinx swear here so don't tell Barney or he gets to whack me in the nuts three times with a whiffle ball bat. ",
      "This is gonna be a long jinx. Like Yom Kippur services long. The only difference is Yom Kippur's a fast and this one's gonna be a slow."
  };

  @Override public List<Mail> generateMails() {

    int id = 0;
    int mailSize = 50;
    List<Mail> mails = new ArrayList<>(mailSize);

    long day = 24 * 60 * 60 * 1000;
    long timestamp = System.currentTimeMillis();

    for (int i = 0; i < mailSize; i++, timestamp -= day) {

      String quotes[] = barneyQuotes;
      Person sender = Person.BARNEY;

      switch (i % 5) {

        case 1:
          sender = Person.LILY;
          quotes = lilyQuotes;
          break;

        case 3:
          sender = Person.MARSHALL;
          quotes = marshallQuotes;
          break;

        case 4:
          sender = Person.ROBIN;
          quotes = robinQuotes;
          break;

        default: // 0 and 2 is barney
          break;
      }

      String label;
      if (i < 40) {
        label = Label.INBOX;
      } else if (i < 45) {
        label = Label.SPAM;
      } else {
        label = Label.TRASH;
      }

      mails.add(new Mail().id(id++)
          .date(new Date(timestamp))
          .label(label)
          .read((i % 6) != random.nextInt(6))
          .receiver(Person.TED)
          .sender(sender)
          .subject(randomString(subjects))
          .text(generateMsg(quotes)));
    }

    /*
    // SENT
    Person[] receiver = { barney, marshall, lily, robin };
    for (int i = 0; i < 10; i++, timestamp -= day) {
      mails.add(new Mail().id(id++)
          .date(new Date(timestamp))
          .label(Label.SENT)
          .read(true)
          .receiver(receiver[randomIndex(receiver)])
          .sender(ted)
          .subject("RE: " + randomString(subjects))
          .text(generateMsg(tedQuotes)));
    }
    */
    return mails;
  }

  private int randomIndex(Object[] array) {
    return random.nextInt(array.length);
  }

  private String randomString(String[] arr) {
    return arr[randomIndex(arr)];
  }

  private String generateMsg(String[] quotes) {

    StringBuilder message = new StringBuilder(hiTed[randomIndex(hiTed)]);

    HashSet<Integer> used = new HashSet<>();
    for (int i = 0; i < 3; i++) {

      message.append("\n");

      int index = 0;
      do {
        index = randomIndex(quotes);
      } while (used.contains(index));

      message.append(quotes[index]);
      used.add(index);
    }

    return message.toString();
  }

  @Override public Mail generateResponseMail(String senderMail) {

    String quotes[] = null;
    Person sender = null;

    if (senderMail.equals(Person.BARNEY.getEmail())) {
      sender = Person.BARNEY;
      quotes = barneyQuotes;
    } else if (senderMail.equals(Person.LILY.getEmail())) {
      quotes = lilyQuotes;
      sender = Person.LILY;
    } else if (senderMail.equals(Person.MARSHALL.getEmail())) {
      quotes = marshallQuotes;
      sender = Person.MARSHALL;
    } else if (senderMail.equals(Person.ROBIN.getEmail())) {
      quotes = robinQuotes;
      sender = Person.ROBIN;
    } else {
      return null;
    }

    return new Mail().id(0)
        .date(new Date())
        .read(false)
        .receiver(Person.TED)
        .sender(sender)
        .subject(randomString(subjects))
        .text(generateMsg(quotes));
  }
}
