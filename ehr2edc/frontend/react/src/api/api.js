import axios from "axios";

export default {
    posts(url) {
        return {
            getOne: ({id}) => axios.get(`${url}/${id}`),
            getAll: () => axios.get(url),
            update: (toUpdate) => axios.put(url, toUpdate),
            create: (toCreate) => axios.put(url, toCreate),
            delete: ({id}) => axios.delete(`${url}/${id}`)
        }
    }
}