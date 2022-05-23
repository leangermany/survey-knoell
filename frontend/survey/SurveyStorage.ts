import { SurveyCreator } from 'survey-creator-knockout';

export class CreatorStorage {
    static key = 'knoell_survey_storage';
    public static getStorage(): CreatorStorage {
        const sessionStorage = window.sessionStorage;
        let creatorStorageString = sessionStorage.getItem(CreatorStorage.key);
        let creatorStorage: CreatorStorage;
        if (creatorStorageString) {
            console.log('existing storage string: ' + creatorStorageString);
            try {
                let parsedObj = JSON.parse(creatorStorageString);
                creatorStorage = CreatorStorage.ofMap(parsedObj.map);
                console.log('parsed storage: ' + creatorStorage);
            } catch (e) {
                console.error(e);
                creatorStorage = new CreatorStorage();
                console.log('creating new storage in catch');
                CreatorStorage.setStorage(creatorStorage);
            }
        } else {
            creatorStorage = new CreatorStorage();
            console.log('creating new storage in else');
            CreatorStorage.setStorage(creatorStorage);
        }
        return creatorStorage;
    }
    public static setStorage(creatorStorage: CreatorStorage): void {
        const sessionStorage = window.sessionStorage;
        sessionStorage.setItem(CreatorStorage.key, JSON.stringify(creatorStorage));
    }
    public static modifyStorage(callback: (surveyStorage: CreatorStorage) => void) {
        const s: CreatorStorage = CreatorStorage.getStorage();
        callback(s);
        CreatorStorage.setStorage(s);
    }
    public static ofMap(map: any): CreatorStorage {
        let storage: CreatorStorage = new CreatorStorage();
        storage.map = new Map(Object.entries(map));
        return storage;
    }

    /**/

    public static setCreator(key: string, creator: SurveyCreator): void {
        CreatorStorage.modifyStorage(storage => {
            storage.set(key, creator);
        });
    }
    public static getCreator(key: string): SurveyCreator {
        let storage: CreatorStorage = CreatorStorage.getStorage();
        return storage.get(key);
    }
    public static deleteCreator(key: string): void {
        CreatorStorage.modifyStorage(storage => {
            storage.delete(key);
        });
    }

    /**/

    public map: Map<string, any> = new Map();
    public set(key: string, creator: SurveyCreator): void {
        this.map.set(key, creator);
    }
    public get(key: string): SurveyCreator {
        return this.map.get(key);
    }
    public delete(key: string): void {
        this.map.delete(key);
    }

}