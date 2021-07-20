package com.ferelin

import androidx.test.platform.app.InstrumentationRegistry
import com.ferelin.remote.RemoteMediator
import com.ferelin.remote.RemoteMediatorImpl
import com.ferelin.remote.api.ApiManager
import com.ferelin.remote.api.ApiManagerImpl
import com.ferelin.remote.auth.AuthenticationManagerImpl
import com.ferelin.remote.database.RealtimeDatabaseImpl
import com.ferelin.remote.webSocket.connector.WebSocketConnector
import com.ferelin.remote.webSocket.connector.WebSocketConnectorImpl
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.Spy
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class RemoteMediatorImplTest {

    private lateinit var mRemoteMediator: RemoteMediator

    @Spy
    private lateinit var mApiManager: ApiManager

    @Spy
    private lateinit var mWebSocketConnector: WebSocketConnector

    @Before
    fun setUp() {
        mApiManager = mock(ApiManagerImpl::class.java)
        mWebSocketConnector = mock(WebSocketConnectorImpl::class.java)
        val authManager = mock(AuthenticationManagerImpl::class.java)
        val realtimeDb = mock(RealtimeDatabaseImpl::class.java)
        val context = InstrumentationRegistry.getInstrumentation().context

        mRemoteMediator = RemoteMediatorImpl(
            mApiManager,
            mWebSocketConnector,
            authManager,
            realtimeDb,
            context
        )
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
        mRemoteMediator.loadStockHistory(symbol, from, to, resolution)
        verify(mApiManager, times(1)).loadStockHistory(symbol, from, to, resolution)
    }

    @Test
    fun loadCompanyProfile() {
        val symbol = "symbol"
        mRemoteMediator.loadCompanyProfile(symbol)
        verify(mApiManager, times(1)).loadCompanyProfile(symbol)
    }

    @Test
    fun loadCompanyNews() {
        val symbol = "symbol"
        val from = "from"
        val to = "to"
        mRemoteMediator.loadCompanyNews(symbol, from, to)
        verify(mApiManager, times(1)).loadCompanyNews(symbol, from, to)
    }

    @Test
    fun loadCompanyQuote() {
        val symbol = "symbol"
        val position = 1
        mRemoteMediator.sendRequestToLoadPrice(symbol, position, false)
        verify(mApiManager, times(1)).sendRequestToLoadPrice(symbol, position, false)
    }
}