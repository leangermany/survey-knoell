window.KnoellSurvey = {
    storageName: 'survey-58b81561-7526dad06d42',
    has: function(key) {
        const store = this.getStore();
        if (store) {
            const index = store.keys.indexOf(key);
            return index >= 0;
        } else {
            return false;
        }
    },
    put: function(key) {
        const store = this.getStore();
        if (store) {
            store.keys.push(key);
            this.setStore(store);
            return true;
        } else {
            return false;
        }
    },
    getAll: function() {
        const store = this.getStore();
        return store.keys;
    },
    getStore: function(createIfNotExists) {
        if (window.localStorage) {
            const item = window.localStorage.getItem(this.storageName);
            if (item && item.length > 2) {
                return JSON.parse(item);
            } else if (createIfNotExists == null || createIfNotExists) {
                return { creation: Date.now(), keys: new Array() };
            }
        }
        return null;
    },
    setStore: function(store) {
        if (window.localStorage) {
            window.localStorage.setItem(this.storageName, JSON.stringify(store));
        }
    },
    clearStore: function() {
        const store = this.getStore();
        if (window.localStorage) {
            window.localStorage.removeItem(this.storageName);
        }
        return store.keys;
    }
};