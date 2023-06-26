package week3

import TweetReader._

/**
 * A class to represent tweets.
 */
class Tweet(val user: String, val text: String, val retweets: Int) {
  override def toString: String =
    "User: " + user + "\n" +
      "Text: " + text + " [" + retweets + "]"
}

abstract class TweetSet extends TweetSetInterface {

  def filter(p: Tweet => Boolean): TweetSet = filterAcc(p, new Empty)

  def filterAcc(p: Tweet => Boolean, acc: TweetSet): TweetSet

  def union(that: TweetSet): TweetSet = filterAcc(_ => true, that)

  def mostRetweeted: Tweet

  def descendingByRetweet: TweetList

  def incl(tweet: Tweet): TweetSet

  def remove(tweet: Tweet): TweetSet

  def contains(tweet: Tweet): Boolean

  def foreach(f: Tweet => Unit): Unit

  def isEmpty: Boolean

  def selectMostRetweetedTweet(tweet: Tweet): Tweet
}

class Empty extends TweetSet {
  def filterAcc(p: Tweet => Boolean, acc: TweetSet): TweetSet = acc

  def contains(tweet: Tweet): Boolean = false

  def incl(tweet: Tweet): TweetSet = new NonEmpty(tweet, new Empty, new Empty)

  def remove(tweet: Tweet): TweetSet = this

  def foreach(f: Tweet => Unit): Unit = ()

  override def mostRetweeted: Tweet = null

  override def descendingByRetweet: TweetList = Nil

  override def isEmpty = true

  override def selectMostRetweetedTweet(tw: Tweet): Tweet = new Tweet("", "", 0)
}

class NonEmpty(tweet: Tweet, left: TweetSet, right: TweetSet) extends TweetSet {

  def filterAcc(p: Tweet => Boolean, acc: TweetSet): TweetSet = {
    val twAcc = if (p(tweet)) acc.incl(tweet) else acc
    val twLeft = left.filterAcc(p, twAcc)
    right.filterAcc(p, twLeft)
  }

  def contains(x: Tweet): Boolean =
    if (x.text < tweet.text) left.contains(x)
    else if (tweet.text < x.text) right.contains(x)
    else true

  def incl(x: Tweet): TweetSet = {
    if (x.text < tweet.text) new NonEmpty(tweet, left.incl(x), right)
    else if (tweet.text < x.text) new NonEmpty(tweet, left, right.incl(x))
    else this
  }

  def remove(tw: Tweet): TweetSet =
    if (tw.text < tweet.text) new NonEmpty(tweet, left.remove(tw), right)
    else if (tweet.text < tw.text) new NonEmpty(tweet, left, right.remove(tw))
    else left.union(right)

  def foreach(f: Tweet => Unit): Unit = {
    f(tweet)
    left.foreach(f)
    right.foreach(f)
  }

  override def isEmpty = false

  override def mostRetweeted: Tweet = selectMostRetweetedTweet(tweet)

  override def descendingByRetweet: TweetList = {
    new Cons(mostRetweeted, remove(mostRetweeted).descendingByRetweet)
  }

  override def selectMostRetweetedTweet(tweet: Tweet): Tweet = {

    val tLeft = left.selectMostRetweetedTweet(tweet)
    val tRight = right.selectMostRetweetedTweet(tweet)

    val tweet2 = if (tRight.retweets > tLeft.retweets) tRight else tLeft
    if (tweet2.retweets > tweet.retweets) tweet2 else tweet
  }
}

trait TweetList {
  def head: Tweet

  def tail: TweetList

  def isEmpty: Boolean

  def foreach(f: Tweet => Unit): Unit =
    if (!isEmpty) {
      f(head)
      tail.foreach(f)
    }
}

object Nil extends TweetList {
  def head = throw new java.util.NoSuchElementException("head of EmptyList")

  def tail = throw new java.util.NoSuchElementException("tail of EmptyList")

  def isEmpty = true
}

class Cons(val head: Tweet, val tail: TweetList) extends TweetList {
  def isEmpty = false
}


object GoogleVsApple {
  val google = List("android", "Android", "galaxy", "Galaxy", "nexus", "Nexus")
  val apple = List("ios", "iOS", "iphone", "iPhone", "ipad", "iPad")

  lazy val googleTweets: TweetSet = filterKeywords(google)
  lazy val appleTweets: TweetSet = filterKeywords(apple)

  /**
   * A list of all tweets mentioning a keyword from either apple or google,
   * sorted by the number of retweets.
   */
  lazy val trending: TweetList = googleTweets.union(appleTweets).descendingByRetweet

  private def filterKeywords(text: List[String]) = TweetReader.allTweets.filter(w => text.exists(w.text.contains(_)))
}

object Main extends App {
  // Print the trending tweets
  GoogleVsApple.trending foreach println
}
