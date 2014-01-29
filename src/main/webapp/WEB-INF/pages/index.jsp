<%@include file="public/header.jsp" %>

    <div id="user-info" class="row">
        <div class="col-md-4 col-md-offset-4 alert alert-success">
            <div class="form-horizontal">
                <div class="form-group">
                    <label for="player-name" class="control-label col-md-3">Player</label>
                    <div class="col-md-6">
                        <input id="player-name" type="text" class="form-control" placeholder="Player1" />
                    </div>
                </div>
                <div class="form-group">
                    <label for="level" class="control-label col-md-3">Level</label>
                    <div id="level" class="col-md-9">
                        <label class="radio-inline">
                            <input type="radio" class="level" name="level" value="beginner" checked />
                            Beginner
                        </label>
                        <label class="radio-inline">
                            <input type="radio" class="level" name="level" value="expert" />
                            Expert
                        </label>
                    </div>
                </div>
                <div class="form-group">
                    <label for="Gender" class="control-label col-md-3">Gender</label>
                    <div id="gender" class="col-md-9">
                        <label class="radio-inline">
                            <input type="radio" class="gender" name="gender" value="male" checked />
                            Male
                        </label>
                        <label class="radio-inline">
                            <input type="radio" class="gender" name="gender" value="female" />
                            Female
                        </label>
                    </div>
                </div>
                <hr />
                <div class="form-group">
                    <button id="start-game" class="btn btn-success col-md-offset-6">Start Game</button>
                </div>
            </div>
        </div>
    </div>

    <div id="content" class="row">
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
            <div id="dealer-part" class="row">
                <strong>Dealer</strong>
                <div id="dealer-card" class="col-md-offset-1" style="height: 110px;">
                    <img src="/img/poker/11.jpg" class="img-thumbnail" width="80px" />
                    <img src="/img/poker/0.jpg" class="img-thumbnail"  width="80px"/>
                </div>

            </div>
            <hr />
            <div id="player-part" class="row">
                <strong>Player</strong>
                <div id="player-card" class="col-md-offset-1" style="height: 110px;">
                    <img src="/img/poker/11.jpg" class="img-thumbnail" width="80px" />
                    <img src="/img/poker/10.jpg" class="img-thumbnail" width="80px" />
                    <img src="/img/poker/10.jpg" class="img-thumbnail" width="80px" />
                    <img src="/img/poker/10.jpg" class="img-thumbnail" width="80px" />
                    <img src="/img/poker/10.jpg" class="img-thumbnail" width="80px" />
                </div>
            </div>
            <hr />
            <div id="operation-board" class="col-md-10 jumbotron" style="padding: 20px;">
                <strong><small>Operation Platform</small></strong>
                <div id="board-before-start">
                    <div id="stack-btn" class="btn-group col-md-2">
                        <button type="button" class="btn btn-warning dropdown-toggle" data-toggle="dropdown">
                            <span id="stack-btn-text"> 注金：$10 </span>
                            <span class="caret"></span>
                        </button>
                        <ul class="dropdown-menu" role="menu" style="">
                            <li class="col-md-offset-1"> $10 </li>
                            <li class="col-md-offset-1"> $50 </li>
                            <li class="col-md-offset-1"> $100 </li>
                        </ul>
                    </div>
                    <div class="pull-right">
                        <button id="open-cards" class="btn btn-primary"> 开牌 </button>
                    </div>
                </div>
                <br/>
                <div id="board-playing">
                    <button id="hit" class="btn btn-info non-first-round"> 要牌 </button>
                    <button id="stand" class="btn btn-warning non-first-round"> 停牌 </button>
                    <button id="double" class="btn btn-success first-round"> 双倍 </button>
                    <button id="surrender" class="btn btn-danger pull-right first-round">
                        投降
                        <span class="glyphicon glyphicon-flag"></span>
                    </button>
                </div>
            </div>
        </div>
        <div id="dealer-account" style="height: 130px;">
            <img src="/img/avatar.jpg" />
            <h4><small> Dealer:
                <a target="_blank" href="http://blog.chxj.name">Tony </a>
            </small></h4>
            <h4><small> Level:
                <span id="level-show" class="label label-success">Beginner</span>
            </small></h4>
        </div>
        <hr />
        <div id="player-account" class="col-md-2 jumbotron" style="padding: 10px;">
            <strong><small> Account </small></strong>
            <small><span class="glyphicon glyphicon-user"> </span></small>
            <img id="player-avatar-show" src="" width="92px" alt=""/>
            <h4><small> Player : <span id="player-name-show">Tony</span> </small></h4>
            <h4><small> Balance : $<span id="balance-show">1000</span></small></h4>
            <h3><small>
                Need Help?
            </small></h3>
            <span id="advisor" class="label label-info" style="cursor: pointer;"
                  data-toggle="popover" data-placement="left" data-container="#advisor-tooltip"
                  data-content="I advise you to 'Hit'" title="My Lord">
                <small>
                    Advisor
                    <span class="glyphicon glyphicon-tag"></span>
                </small>
            </span>
            <div id="advisor-tooltip" style="font-size: 14px;"></div>
        </div>

        <div id="powered-by" class="col-md-2" style="margin-top: 10px;">
            <h4>
                <small> Fetch the code from
                    <a target="_blank" href="http://github.com/chenxiaojing123/BlackJack">
                        <img src="/img/github.ico" width="24px" alt=""/>
                    </a>
                </small>
            </h4>
        </div>
    </div>


<%@include file="public/footer.jsp" %>
