import { html, css, LitElement, PropertyDeclarations } from 'lit';
import 'survey-core/defaultV2.min.css';
import 'survey-creator-core/survey-creator-core.min.css';
import { SurveyCreator } from 'survey-creator-knockout';
import { Notification } from '@vaadin/notification';

class SurveyCreatorImpl extends LitElement {
    creatorOptions = { showLogicTab: true, isAutoSave: true, haveCommercialLicense: true };
    creator: SurveyCreator;

    constructor() {
        super();
        this.creator = new SurveyCreator(this.creatorOptions);
        this.creator.saveSurveyFunc = (saveNo: number, callback: Function) => {
            let completedEvent = new CustomEvent('saveSurvey', { detail: { json: this.creator.JSON, saveNo } });
            this.dispatchEvent(completedEvent);
            //Notification.show('Saved!', { duration: 1000, theme: 'success' });
        }
        this.setAutoSaveDelay(3000);
        this.showSimulatorInPreviewTab(true);
        this.creator.JSON = this.defaultJSON();
        window.addEventListener('resize', e => {
            this.creator.render(this.id);
        });
        //this.showTranslationTab(true);
    }
    connectedCallback() {
        super.connectedCallback()
        setTimeout(() => {
            this.creator.render(this.id);
        }, 0);
    }
    render() {
        return html`<div id='placeholder-${this.id}'></div>`;
    }
    createRenderRoot() {
        return this;
    }
    is() {
        return 'survey-creator-impl';
    }
    getText(): string {
        return this.creator.text;
    }
    setText(text: string): void {
        this.creator.text = text;
    }
    changeText(text: string): void {
        this.creator.changeText(text);
    }
    setAutoSaveDelay(millis: number): void {
        this.creator.autoSaveDelay = millis;
    }
    getAutoSaveDelay(): number {
        return this.creator.autoSaveDelay;
    }
    setLocale(localeShortage: string): void {
        if (localeShortage.length != 2) {
            throw `The given locale ${localeShortage} must have a length of two characters like 'en' or 'de'`;
        }
        this.creator.locale = localeShortage;
    }
    getLocale(): string {
        return this.creator.locale;
    }
    setReadOnly(readOnly: boolean): void {
        this.creator.readOnly = readOnly;
    }
    getReadOnly(): boolean {
        return this.creator.readOnly;
    }
    showPreviewTab(showPreviewTab: boolean): void {
        this.creator.showPreviewTab = showPreviewTab;
    }
    showSimulatorInPreviewTab(showSimulatorInPreviewTab: boolean): void {
        this.creator.showSimulatorInPreviewTab = showSimulatorInPreviewTab;
    }
    showTranslationTab(showTranslationTab: boolean): void {
        this.creator.showTranslationTab = showTranslationTab;
    }
    copyToClipboard(): void {
        let textToCopy = this.creator.text;
        //let notifyFunction = (value: string) => this.$server.notifyClipboardCopySuccess(value);
        if (textToCopy) {
            navigator.clipboard.writeText(textToCopy).then(function () {
                /* clipboard successfully set */
                Notification.show('The configuration was copied to the clipboard', { theme: 'primary' });
            }, function () {
                /* clipboard write failed */
                Notification.show("The configuration couldn't be copied to the clipboard. Please open the Tab 'JSON Editor' and copy the value manually!",
                    { theme: 'error', duration: 5000 });
            }).catch(error => {
                Notification.show("The configuration couldn't be copied to the clipboard. Please open the Tab 'JSON Editor' and copy the value manually!",
                    { theme: 'error', duration: 5000 });
            });
        }
    }
    pasteFromClipboard(): void {
        //let notifyFunction = (value: string) => this.$server.notifyClipboardPasteSuccess(value);
        navigator.clipboard.readText().then(clipText => {
            try {
                JSON.parse(clipText);
                this.changeText(clipText);
                Notification.show('The configuration was pasted from the clipboard', { theme: "primary" });
            } catch (error) {
                Notification.show("The content was no Survey Configuration",
                    { theme: 'error', duration: 3000 });
            }
        }, onRejected => {
            Notification.show("Permission to read the clipboard was denied!",
                { theme: 'error', duration: 3000 });
        }
        ).catch(error => {
            Notification.show("The configuration couldn't be pasted to the clipboard. Please open the Tab 'JSON Editor' and paste the value manually!",
                { theme: 'error', duration: 5000 });
        });
    }
    clear(): void {
        //this.creator.JSON = this.defaultJSON();
        this.changeText(JSON.stringify(this.defaultJSON()));
    }
    defaultJSON(): any {
        return {
            "title": "Survey",
            "description": "Description",
            "logo": "/Knoell_Logo_4c3_slim.png",
            "logoWidth": "200px",
            "logoHeight": "170px",
            "logoPosition": "right",
            "focusOnFirstError": false,
            "completedHtml": "<div style=\"align-items:center;width:100%;display:flex;flex-flow:column;padding:var(--lumo-space-l);box-sizing:border-box;\">\n<img src=\"./tickcirclelinear_106244.svg\" alt=\"completed image\" style=\"max-height: 150px; max-width: 150px;\"><h2>Survey completed</h2><p style=\"text-align: center;\">Thanks for completing this survey provided by knoell.</p><p style=\"text-align: center;\">You do not need to do anything else and you can close this page.</p>\n</div>",
            "pages": [
                {
                    "name": "disclaimer_page",
                    "elements": [
                        {
                            "type": "panel",
                            "name": "disclaimer_panel",
                            "elements": [
                                {
                                    "type": "html",
                                    "name": "question2",
                                    "html": "<h3>Data privacy information</h3>\n<p>As an employer, knoell is legally obligated to inspect all equipment of the employees and to ensure that this is done (according to ArbStättV & BetrSichV & DGUV 3+4). With regard to the planning of this inspection, personal data must be collected.</p>\n<p>For this purpose, it is necessary that the following information is collected and processed based on a controller processor agreement according to Art. 28 of the General Data Protection Regulation (GDPR):<p>\n<ul><li>e-mail address of the employee</li></ul>\n<p>The data collected will only be forwarded to the Coordinator for Occupational Safety and the Head of Quality, Health & Sustainability for internal coordination. Otherwise, data is only passed on to recipients outside the company if legal regulations allow or require this (e.g. Berufsgenossenschaft Rohstoffe und chemische Industrie).</p>\n<p>Due to legal retention obligations, the collected data must be kept for at least two years.</p>\n<p>For further details, see \"General information of the data subject on the collection of personal data as an employee\" (SOP 17-06) in kim.</p>"
                                },
                                {
                                    "type": "checkbox",
                                    "name": "agree_policy_checkbox",
                                    "title": "I unterstand and agree the usage of my data?",
                                    "isRequired": true,
                                    "choices": [
                                        {
                                            "value": "item1",
                                            "text": "I agree with the data policy"
                                        }
                                    ]
                                }
                            ]
                        }
                    ],
                    "title": "Legal Disclaimer",
                    "description": "Information about the usage of you data"
                },
                {
                    "name": "page2",
                    "elements": [
                        {
                            "type": "text",
                            "name": "question1",
                            "title": "Start with your survey here"
                        }
                    ]
                }
            ],
            "showPageNumbers": true
        };
    }
}

customElements.define('survey-creator-impl', SurveyCreatorImpl);