package kr.scala.experiments.tests.serializers

import kr.scala.experiments.tests.AbstractExperimentTest
import scala.pickling._
import scala.pickling.binary._
import scala.reflect.ClassTag

/**
 * PicklingTest
 * @author Sunghyouk Bae
 */
class PicklingTest extends AbstractExperimentTest {

    test("scala pickling basic sample") {
        val person = Person("John Oliver", 36)
        val json = person.pickle
        println(s"Pickling person = $json")

        val converted = json.unpickle[Person]
        assert(converted == person)
    }

    test("scala serialize/deserialize by Scala-Pickle") {
        val pickler = new BinaryPickler[Person]()

        val person = Person("배성혁", 46)

        val bytes = person.pickle.value
        val converted = bytes.unpickle[Person]

        assert(converted == person)
    }
}


case class Person(name: String, age: Int)

class BinaryPickler[T <: Person : ClassTag] {

    implicit val format: BinaryPickleFormat = scala.pickling.binary.pickleFormat
}
