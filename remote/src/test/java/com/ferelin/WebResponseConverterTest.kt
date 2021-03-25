package com.ferelin

import com.ferelin.remote.utilits.Api
import com.ferelin.remote.webSocket.WebResponseConverter
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class WebResponseConverterTest {

    private lateinit var mWebResponseConverter: WebResponseConverter

    @Before
    fun setUp() {
        mWebResponseConverter = WebResponseConverter()
    }

    @Test
    fun additionalMessage() {
        val response = mWebResponseConverter.fromJson(
            FakeRemoteResponses.webSocketSuccessStr,
            FakeRemoteResponses.wabSocketOpenPriceHolder
        )
        Assert.assertTrue(
            response.additionalMessage ==
                    FakeRemoteResponses.wabSocketOpenPriceHolder[FakeRemoteResponses.webSocketResponse.symbol].toString()
        )
    }

    @Test
    fun successResponse() {
        val response = mWebResponseConverter.fromJson(
            FakeRemoteResponses.webSocketSuccessStr,
            FakeRemoteResponses.wabSocketOpenPriceHolder
        )
        Assert.assertTrue(response.responseCode == Api.RESPONSE_OK)
    }

    @Test
    fun tradeNotAvailableResponse() {
        val response = mWebResponseConverter.fromJson(
            FakeRemoteResponses.webSocketNoVolumeStr,
            FakeRemoteResponses.wabSocketOpenPriceHolder
        )
        Assert.assertTrue(response.responseCode == Api.RESPONSE_TRADE_NOT_AVAILABLE)
    }

    @Test
    fun undefinedResponse() {
        val response = mWebResponseConverter.fromJson(
            FakeRemoteResponses.webSocketUndefinedStr,
            FakeRemoteResponses.wabSocketOpenPriceHolder
        )
        Assert.assertTrue(response.responseCode == Api.RESPONSE_UNDEFINED)
    }
}