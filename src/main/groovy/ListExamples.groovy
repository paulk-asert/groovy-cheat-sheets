/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.common.collect.HashBiMap
import com.google.common.collect.TreeMultiset
import org.apache.commons.collections4.bag.HashBag
import org.apache.commons.collections4.bidimap.TreeBidiMap
import org.eclipse.collections.api.factory.Bags
import org.eclipse.collections.api.factory.BiMaps
import org.eclipse.collections.api.factory.Lists

import java.util.concurrent.Executors
import java.util.stream.Collectors
import java.util.stream.Stream
import groovyx.gpars.GParsPool
import groovyx.gpars.GParsExecutorsPool

// declaring lists
var pets    = ['cat', 'canary', 'dog', 'fish', 'gerbil']  // idiomatic Groovy
var nums    = [1, 2, 3, 4] as LinkedList                  // use 'as' for other kinds of list
var primes  = List.of(2, 3, 5, 7)                         // Java 9+ style (immutable)
var range   = 1..4                                        // a range is also a list (immutable)
var bigNums = [1000, 2000].asUnmodifiable()               // unmodifiable (backed by original)
var negNums = [-100, -200].asImmutable()                  // immutable (backed by copy)
assert [pets, nums, primes, range, bigNums, negNums].every { it instanceof List }

// Java methods for list access and properties
assert !pets.isEmpty()
assert pets.size() == 5
assert pets.get(0) == 'cat'
assert pets.contains('dog')
assert pets.containsAll('cat', 'dog')
pets.forEach { assert it.size() > 2 }
assert ['a', 'b', 'a'].indexOf('a') == 0
assert ['a', 'b', 'a'].lastIndexOf('a') == 2

// Groovy extensions for list access and properties
assert pets[0] == 'cat'
assert pets?[0] == 'cat'  // safe indexing returns null if pets was null instead of NPE
assert pets.first() == 'cat'
assert pets.head() == 'cat'
assert pets[-1] == 'gerbil'
assert pets[1..3] == ['canary', 'dog', 'fish']
assert pets[3..1] == ['fish', 'dog', 'canary']  // reverse range
assert pets[1, 3, 3] == ['canary', 'fish', 'fish']  // arbitrary collection
assert pets[0..1, [3, 3]] == ['cat', 'canary', 'fish', 'fish']  // nested collections
assert [1, 2, 3, 1].count(1) == 2
assert [1, 2, 3, 4].min() == 1
assert [1, 2, 3, 4].max() == 4
assert [1, 2, 3, 4].sum() == 10
assert [1, 2, 3, 4].average() == 2.5
[1, 2, 3, 4].eachWithIndex{ val, idx -> assert val == idx + 1 }
def cpets = pets[0..1]
assert cpets == ['cat', 'canary']
assert pets.findAll { it =~ /c.*/ } == cpets
assert pets.find { it =~ /c.*/ } == cpets[0]
assert pets.grep(~/c.*/) == cpets
assert cpets.min { it.size() } == 'cat'  // cpet with smallest size name
assert cpets.max { it.size() } == 'canary'  // cpet with largest name
assert pets.groupBy { it.size() } == [3: ['cat', 'dog'], 4: ['fish'], 6: ['canary', 'gerbil']]
assert pets.countBy { it.size() } == [3: 2, 4: 1, 6: 2]
assert pets.sum{ it.size() } == 22  // total size of all pet names
assert pets.average{ it.size() } == 4.4  // average size of pet names
assert pets.indices == 0..4
assert pets.findIndexValues { it.size() == 6 } == [1, 4]
assert cpets.indexed() == [0: 'cat', 1: 'canary']
assert cpets.withIndex()*.toList() == [['cat', 0], ['canary', 1]]


// methods from Java for mutating lists
pets.remove(2);                        assert pets == ['cat', 'canary', 'fish', 'gerbil']  // remove by index
pets.remove('fish');                   assert pets == ['cat', 'canary', 'gerbil']  // remove by element
pets.removeIf(p -> p.startsWith("c")); assert pets == ['gerbil']  // remove by condition
pets.clear();                          assert pets.isEmpty()  // make empty
pets.add("kangaroo");                  assert pets == ['kangaroo']  // add element
pets.add(0, "koala");                  assert pets == ['koala', 'kangaroo']  // add element at index position
pets.addAll(['ant', 'bee']);           assert pets == ['koala', 'kangaroo', 'ant', 'bee']  // add collection
pets.addAll(1, ['ant']);               assert pets == ['koala', 'ant', 'kangaroo', 'ant', 'bee']  // add collection at index
pets.removeAll(['ant', 'flea']);       assert pets == ['koala', 'kangaroo', 'bee']  // remove from collection
pets.retainAll(['bee', 'koala']);      assert pets == ['koala', 'bee']  // retain from collection
pets.set(0, "zebra");                  assert pets == ['zebra', 'bee']  // set element at index
pets.replaceAll(String::toUpperCase);  assert pets == ['ZEBRA', 'BEE']  // replace elements by unary operator
pets.sort(Comparator.naturalOrder());  assert pets == ['BEE', 'ZEBRA']  // sort

// Groovy extensions for mutating methods
pets << 'rock';                        assert pets == ['BEE', 'ZEBRA', 'rock']  // leftShift append
pets += ['rabbit', 'rock', 'hare'];    assert pets == ['BEE', 'ZEBRA', 'rock', 'rabbit', 'rock', 'hare']  // append collection
pets.unique();                         assert pets == ['BEE', 'ZEBRA', 'rock', 'rabbit', 'hare']  // remove duplicates
pets.sort{ it.size() };                assert pets == ['BEE', 'rock', 'hare', 'ZEBRA', 'rabbit']  // sort by size
pets[0] = 'ant';                       assert pets == ['ant', 'rock', 'hare', 'ZEBRA', 'rabbit']  // replace element by index
pets[1..3] = ['snake', 'SNAKE'];       assert pets == ['ant', 'snake', 'SNAKE', 'rabbit']  // replace range of elements
pets.unique{it.toLowerCase() };        assert pets == ['ant', 'snake', 'rabbit']  // remove duplicates ignoring case
pets.reverse(true);                    assert pets == ['rabbit', 'snake', 'ant']  // flip
pets.shuffle();                        assert pets.size() == 3;  // shuffle elements; resulting order will vary
def dice = [1, 2, 3, 4, 5, 6]
dice.removeAt(2);                      assert dice == [1, 2, 4, 5, 6]
dice.removeElement(2);                 assert dice == [1, 4, 5, 6]
dice.removeLast();                     assert dice == [1, 4, 5]
dice.swap(0, 2);                       assert dice == [5, 4, 1]

// Functions which return new lists or values
assert [1, 2, 3] + [1] == [1, 2, 3, 1]
assert [1, 2, 3, 1] - [1] == [2, 3]
assert [1, 2, 3] * 2 == [1, 2, 3, 1, 2, 3]
assert [1, [2, 3]].flatten() == [1, 2, 3]
assert [1, 2, 3].disjoint([4, 5, 6])
assert [1, 2, 3].intersect([4, 3, 1]) == [1, 3]
assert [1, 2, 3].collect { it + 3 } == [4, 5, 6]
assert [1, 2, 3, 4].collect { 1 } == [1, 1, 1, 1]
assert [4, 2, 1, 3].findAll { it % 2 == 0 } == [4, 2]
assert [1, 2, 3, 4].take(3) == [1, 2, 3]
assert [1, 2, 3, 4].takeRight(3) == [2, 3, 4]
assert [1, 2, 3, 4].takeWhile{ it < 3 } == [1, 2]
assert [1, 2, 3, 4].drop(2) == [3, 4]
assert [1, 2, 3, 4].dropRight(2) == [1, 2]
assert [1, 2, 3, 4].dropWhile{it < 3 } == [3, 4]
assert [1, 2, 3, 4].join('-') == '1-2-3-4'
assert [1, 2, 3, 4].tail() == [2, 3, 4]
assert [1, 2, 3, 4].init() == [1, 2, 3]
assert [1, 2, 3, 4].tails() == [[1, 2, 3, 4], [2, 3, 4], [3, 4], [4], []]
assert [1, 2, 3, 4].inits() == [[1, 2, 3, 4], [1, 2, 3], [1, 2], [1], []]
assert [1, 2, 3, 4].reverse() == [4, 3, 2, 1]
assert [1, 2, 3, 1].toUnique() == [1, 2, 3]
assert [1, 2, 3, 1].toSorted() == [1, 1, 2, 3]
assert [1, 2, 3, 4].collect { it * 2 } == [2, 4, 6, 8]
assert [[1, 2], [3, 4]].collectNested { it * 2 } == [[2, 4], [6, 8]]
def squaresAndCubesOfEvens = { it % 2 ? [] : [it**2, it**3] }
assert [1, 2, 3, 4].collectMany(squaresAndCubesOfEvens) == [4, 8, 16, 64]
assert [1, 2, 3, 4].any { it > 3 }
assert [1, 2, 3, 4].every { it < 5 }
assert ![1, 2, 3, 4].every { it > 3 }
assert [1, 2, 3, 4].find { it > 2 } == 3
assert [1, 2, 3, 4].findAll { it > 2 } == [3, 4]
assert [1, 2, 3, 4].findIndexOf { it > 2 } == 2
assert [1, 2, 3, 1].findLastIndexOf { it > 2 } == 2
assert [1, 2, 3, 4].inject { acc, i -> acc + i } == 10
assert (1..10).collate(3)  == [[1, 2, 3], [4, 5, 6], [7, 8, 9], [10]]
assert (1..10).chop(1, 3, 2, -1)  == [[1], [2, 3, 4], [5, 6], [7, 8, 9, 10]]
assert [1,2,3].permutations().toList() == [
        [1, 2, 3], [3, 2, 1], [2, 1, 3], [3, 1, 2], [1, 3, 2], [2, 3, 1]
]
def matrix = [['a', 'b'], [ 1 ,  2 ]]
assert matrix.transpose()    == [ ['a', 1], ['b', 2] ]
assert matrix.combinations() == [ ['a', 1], ['b', 1], ['a', 2], ['b', 2] ]
assert [1, 2, 3].subsequences()*.toList() == [[1], [1, 2, 3], [2], [2, 3], [1, 2], [3], [1, 3]]
def answers = [1, 2, 3].withDefault{ 42 }
assert answers[2] == 3 && answers[99] == 42

// stream
pets = ['cat', 'canary', 'dog', 'fish', 'gerbil']
assert pets.stream().filter(p -> p.size() == 3).map(String::toUpperCase).toList() == ['CAT', 'DOG']
assert pets.stream().map(p -> p.size()).distinct().sorted().toList() == [3, 4, 6]  // ordered pet name sizes
assert nums.stream().reduce{ a, b -> a + b }.get() == 10
assert (1..10).stream().skip(3).limit(5).filter(i -> i % 2 == 0).map(i -> i ** 2).toList() == [16, 36, 64]
assert [1, 2, 3, 4].stream().flatMap(i -> i % 2 ? Stream.empty() : Stream.of(i**2, i**3)).toList() == [4, 8, 16, 64]
assert pets.stream().collect(Collectors.groupingBy(p -> p.size())) == [3:['cat', 'dog'], 4:['fish'], 6:['canary', 'gerbil']]
assert [1, 2, 3, 4].stream().map(Integer::toString).collect(Collectors.joining('-')) == '1-2-3-4'
Arrays.stream(0..9 as int[]).summaryStatistics().with {
    assert sum == 45 && min == 0 && max == 9 && average == 4.5 && count == 10
}
assert pets.stream().allMatch(w -> w ==~ /.*[aeiou].*/)  // all pet names contain a vowel

// GQ
// squares of odd numbers between 1 and 5
assert [1, 9, 25] == GQL {
    from n in 1..5 where n % 2 != 0 select n ** 2
}

// group pets by name size
assert ["3:[cat, dog]", "4:[fish]", "6:[canary, gerbil]"] == GQL {
    from p in pets
    groupby p.size() as size
    select size, agg(p) as names
}*.with{ "$it.size:$it.names" }

// Apache Commons Collections
var six = [six: 6] as TreeBidiMap
assert six.inverseBidiMap() == [6: 'six']
var bag = new HashBag(['one'] * 6)
bag.remove('one', 2)
assert bag.getCount('one') == 4

// Guava
var set = TreeMultiset.create([1, 2, 3])
assert set == TreeMultiset.create([3, 2, 1])
set.addAll([1, 3, 5])
assert set.size() == 6 && set.elementSet().size() == 4
assert set.toList() == [1, 1, 2, 3, 3, 5]
var bimap = HashBiMap.create()
bimap.five = 5
assert bimap.inverse()[5] == 'five'

// Eclipse Collections
var certainties = Lists.immutable.of('death', 'taxes')
assert certainties.reduce{ a, b -> "$a & $b" }.get() == 'death & taxes'
var numBag = Bags.immutable.with('One', 'One', 'Two', 'Three')
assert numBag.toMapOfItemToCount() == [One:2, Two:1, Three:1]
var biMap = BiMaps.immutable.with(6, "six", 2, "two")
assert biMap.inverse().six == 6

// gpars
// calculate squares of odd numbers from input list
assert (1..5).parallelStream().filter{ it % 2 != 0 }.map(n -> n ** 2).toList() == [1, 9, 25]

GParsPool.withPool {
    assert (1..5).findAllParallel{ it % 2 }.collectParallel{ it ** 2 } == [1, 9, 25]
    assert (1..5).parallel.filter{ it % 2 }.map{ it ** 2 }.collection == [1, 9, 25]
}

/*
GParsExecutorsPool.withExistingPool(Executors.newVirtualThreadPerTaskExecutor()) {
    assert (1..5).findAllParallel{ it % 2 }.collectParallel{ it ** 2 } == [1, 9, 25]
}
/* */
