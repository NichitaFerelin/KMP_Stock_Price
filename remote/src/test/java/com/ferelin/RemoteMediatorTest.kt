package com.ferelin

import com.ferelin.remote.RemoteMediator
import com.ferelin.remote.RemoteMediatorHelper
import com.ferelin.remote.network.NetworkManager
import com.ferelin.remote.network.NetworkManagerHelper
import com.ferelin.remote.webSocket.WebSocketConnector
import com.ferelin.remote.webSocket.WebSocketConnectorHelper
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.Spy
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class RemoteMediatorTest {

    private lateinit var mRemoteMediator: RemoteMediatorHelper

    @Spy
    private lateinit var mNetworkManager: NetworkManagerHelper

    @Spy
    private lateinit var mWebSocketConnector: WebSocketConnectorHelper

    @Before
    fun setUp() {
        mNetworkManager = mock(NetworkManager::class.java)
        mWebSocketConnector = mock(WebSocketConnector::class.java)
        mRemoteMediator = RemoteMediator(mNetworkManager, mWebSocketConnector)
    }

    @Test
    fun openConnection() {
        val token = "token"
        mRemoteMediator.openWebSocketConnection(token)
        verify(mWebSocketConnector, times(1)).openWebSocketConnection(token)
    }

    @Test
    fun closeConnection() {
        mRemoteMediator.closeWebSocketConnection()
        verify(mWebSocketConnector, times(1)).closeWebSocketConnection()
    }

    @Test
    fun subscribeItem() {
        val symbol = "symbol"
        val price = 100.0
        mRemoteMediator.subscribeItemOnLiveTimeUpdates(symbol, price)
        verify(mWebSocketConnector, times(1)).subscribeItemOnLiveTimeUpdates(symbol, price)
    }

    @Test
    fun unsubscribeItem() {
        val symbol = "symbol"
        mRemoteMediator.unsubscribeItemFromLiveTimeUpdates(symbol)
        verify(mWebSocketConnector, times(1)).unsubscribeItemFromLiveTimeUpdates(symbol)
    }

    @Test
    fun loadStockCandles() {
        val symbol = "symbol"
        val from = 100L
        val to = 200L
        val resolution = "D"
        mRemoteMediator.loadStockCandles(symbol, from, to, resolution)
        verify(mNetworkManager, times(1)).loadStockCandles(symbol, from, to, resolution)
    }

    @Test
    fun loadCompanyProfile() {
        val symbol = "symbol"
        mRemoteMediator.loadCompanyProfile(symbol)
        verify(mNetworkManager, times(1)).loadCompanyProfile(symbol)
    }

    @Test
    fun loadCompanyNews() {
        val symbol = "symbol"
        val from = "from"
        val to = "to"
        mRemoteMediator.loadCompanyNews(symbol, from, to)
        verify(mNetworkManager, times(1)).loadCompanyNews(symbol, from, to)
    }

    @Test
    fun loadCompanyQuote() {
        val symbol = "symbol"
        val position = 1
        mRemoteMediator.loadCompanyQuote(symbol, position)
        verify(mNetworkManager, times(1)).loadCompanyQuote(symbol, position)
    }
}