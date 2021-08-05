function CsrfService() {
    this.sefCsrfHeader = sefCsrfHeader;

    function sefCsrfHeader(e, xhr) {
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        if (typeof header !== 'undefined' && typeof token !== 'undefined') {
            xhr.setRequestHeader(header, token);
        }
    }
}