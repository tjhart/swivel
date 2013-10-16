/**
 * Development notes:
 *
 * At one point, there were factory classes responsible for creating Shunts and Stubs from maps.
 *
 * But then I had to implement Configuration#load - and load an entire configuration from json. As I started
 * To implement that, I quickly ran into MVC boundary issues fighting against re-usability and leaky-abstraction issues:
 *      1. Leaving factories in place meant that the controller *and* model would both need to know about
 *          the inner workings of Configuration - leaky
 *      2. Having the model objects be able to marshal and unmarshal themselves from maps gave me more
 *          re-use, at the cost of making it harder to test the model - less opportunities to inject
 *          mocks.
 *
 *  Given the close relationship between maps and json anyway, it seemed that the model abstraction leaked
 *  nearly all the way to the UI. But the ease of creating the maps was less of a burden at the time than
 *  dealing with duplicated code and leaky controller abstractions for 1 above, so I went with 2.
 */
package com.tjh.swivel.model;

