<%@include file="public/header.jsp" %>

    <link rel="stylesheet" href="/css/bs-callout.css"/>
</head>

<body>
    <div class="container">

        <div class="row page-header">
            <div class="col-md-3 col-md-offset-4">
                <h1 class="col-md-offset-1"> Black Jack </h1>
            </div>
        </div>

        <div class="row">
            <div id="description" class="col-md-3">
                <div>
                    <strong>Rules</strong>
                </div>
               <p> <small>黑杰克(BlackJack):2张手牌相加为21点（A+10）</small></p>
                <p><small>要牌(Hit):再得一张牌,庄家手牌小于17点必须要牌</small></p>
                <p><small>停牌(Stand):不再要牌,庄家手牌达到17必须停牌</small></p>
                <p><small>爆牌(Bust):手牌总数大于21点,爆牌则输掉注金</small></p>
                <p><small>分牌(Split):当玩家拿到2张一样的手牌时可以选择分牌,
                    即再下一注并将2张牌分为单独的2副牌,如果拿到2张A,
                    分牌后只能再各要一张,如果拿到黑杰克也只能算普通的21点</small></p>
                <p><small>双倍(Double):玩家第一回合可双倍下注,然后再拿一张牌</small></p>
                <p><small>保险(Insurance):如果庄家明牌为A,玩家可以选择买保险,
                    如果庄家拿到黑杰克,玩家将赢得2倍注金,否则输掉保险金</small></p>
                <p><small>投降(Surrender):第一回合且庄家明牌不为A时，玩家可投降并收回一半注金</small></p>

                <strong>Victories</strong>
                <p><small>基本:玩家点数大于庄家,赢1倍注金</small></p>
                <p><small>黑杰克:玩家获得黑杰克时,赢2倍注金</small></p>
                <p><small>特奖:玩家牌为同花色6,7,8或3张7时,赢3倍注金</small></p>
                <p><small>五龙:当庄家明牌不为A,而玩家手中有5张牌且没有爆牌，可赢0.5倍注金</small></p>
            </div>

            <div id="card-board" class="col-md-7" style="padding-left: 40px;">
                <div id="dealer-card" class="row">
                    <strong>Dealer</strong> <br />
                    <img src="/img/avatar.jpg" alt=""/>
                    <img src="/img/poker/11.jpg" class="img-thumbnail" width="80px" />
                    <img src="/img/poker/0.jpg" class="img-thumbnail"  width="80px"/>
                </div>
                <hr />
                <div id="player-card" class="row">
                    <strong>Player</strong> <br />
                    <img src="/img/avatar.jpg" alt=""/>
                    <img src="/img/poker/11.jpg" class="img-thumbnail" width="80px" />
                    <img src="/img/poker/10.jpg" class="img-thumbnail" width="80px" />
                    <img src="/img/poker/10.jpg" class="img-thumbnail" width="80px" />
                    <img src="/img/poker/10.jpg" class="img-thumbnail" width="80px" />
                    <img src="/img/poker/10.jpg" class="img-thumbnail" width="80px" />
                </div>
                <hr />
                <div id="operation-board" class="col-md-10 jumbotron" style="padding: 20px;">
                    <div id="stack-btn" class="btn-group col-md-2">
                        <button type="button" class="btn btn-warning dropdown-toggle" data-toggle="dropdown">
                            注金：$5 <span class="caret"></span>
                        </button>
                        <ul class="dropdown-menu" role="menu" style="">
                            <li class="col-md-offset-1"> $10 </li>
                            <li class="col-md-offset-1"> $50 </li>
                            <li class="col-md-offset-1"> $100 </li>
                        </ul>
                    </div>
                    <div class="pull-right">
                        <button class="btn btn-primary"> 开牌 </button>
                    </div>
                </div>
            </div>

            <div id="account-board" class="col-md-2 jumbotron" style="padding: 20px;">
                <strong> Your Account </strong>
                <img src="/img/avatar.jpg" alt=""/>
                <h4><small> Player : Tony </small></h4>
                <h4><small> Balance : $1000 </small></h4>
                <h3><small>
                    Need Help?
                </small></h3>
                <span class="label label-info"><small>Advisor</small></span>

            </div>
        </div>

    </div>

    <script>
        seajs.use(['jquery','index']);
    </script>

<%@include file="public/footer.jsp" %>
