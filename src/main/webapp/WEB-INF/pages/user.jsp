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