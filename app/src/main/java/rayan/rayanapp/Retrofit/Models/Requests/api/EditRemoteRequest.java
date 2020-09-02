package rayan.rayanapp.Retrofit.Models.Requests.api;

import java.util.List;

import rayan.rayanapp.Data.Remote;
import rayan.rayanapp.Retrofit.Models.Responses.api.Topic;

public class EditRemoteRequest {
    private String remoteHub_id;
    private Remote_ remote;

    public EditRemoteRequest(Remote remote) {
        this.remote = new Remote_();
        this.remote.setName(remote.getName());
        this.remote.set_id(remote.getId());
        this.remote.setVisible(remote.isVisibility());
        this.remote.setAccessible(remote.isAccessible());
        this.remote.setFavorite(remote.isFavorite());
        this.remoteHub_id = remote.getRemoteHubId();
    }

    private class Remote_{
        private String _id,name;
        private Topic topic;
        private List<String> remoteDatas;
        private String remoteHubId;
        private String type;
        private String groupId;
        private boolean accessible,learned,visible,favorite;


        public void setFavorite(boolean favorite) {
            this.favorite = favorite;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setTopic(Topic topic) {
            this.topic = topic;
        }

        public void setRemoteDatas(List<String> remoteDatas) {
            this.remoteDatas = remoteDatas;
        }

        public void setRemoteHubId(String remoteHubId) {
            this.remoteHubId = remoteHubId;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public void setAccessible(boolean accessible) {
            this.accessible = accessible;
        }

        public void setLearned(boolean learned) {
            this.learned = learned;
        }

        public void setVisible(boolean visible) {
            this.visible = visible;
        }
    }
}
